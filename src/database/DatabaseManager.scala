package database

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import config.Configuration
import logging.{LogLevel, Logger}
import models.{Config, DatabaseConfig}
import stats.StatsManager

/**
  * Manager responsible for handling interaction with the MySQL server.
  */
class DatabaseManager {
  private var connection: Connection = _
  val conf: Config = Configuration.getConfig

  /**
    * Constructor
    * Define connection
    */
  def setConnection() {
    connection = DriverManager.getConnection(getConnectionString, conf.database.username, conf.database.password)
  }

  /**
    * Construct a connection string from configuration.
    *
    * @return string formatted connection string
    */
  private def getConnectionString: String = {
    val db: DatabaseConfig = conf.database
    s"jdbc:mysql://${db.host}:${db.port}/${db.database}?verifyServerCertificate=false&useSSL=true"
  }

  /**
    * Get a players UUID from world ids.
    *
    * @param x x coordinate of the world
    * @param z z coordinate of the world
    * @return string|null uuid of the player
    */
  def getPlayerUUIDFromWorld(x: String, z: String): String = {
    if (connection == null) this.setConnection()
    val plotTable: String = conf.database.plotTable
    val query: String = s"SELECT owner FROM $plotTable WHERE plot_id_x = ? AND plot_id_z = ? LIMIT 1"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1, x)
    statement.setString(2, z)
    logStatement(statement)
    val resultSet: ResultSet = statement.executeQuery()
    StatsManager.increaseCounter("sql-queries")
    if (resultSet.next()) {
      return resultSet.getString("owner")
    }
    null
  }

  /**
    * Delete a plot record from the database
    *
    * @param x    x coordinate of the plot
    * @param z    z coordinate of the plot
    * @param uuid uuid of the plot owner
    * @return boolean true if deleted, false otherwise
    */
  def deletePlotEntry(x: String, z: String, uuid: String): Boolean = {
    if (connection == null) this.setConnection()
    val plotTable: String = conf.database.plotTable
    val query: String = s"DELETE FROM $plotTable WHERE plot_id_x = ? AND plot_id_z = ? AND owner = ?"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1, x)
    statement.setString(2, z)
    statement.setString(3, uuid)
    StatsManager.increaseCounter("sql-queries")

    logStatement(statement)
    val result = statement.executeUpdate()
    if (result == 0) Logger.error("Failed to execute last query")
    result != 0
  }

  private def logStatement(preparedStatement: PreparedStatement): Unit = {
    Logger.write(s"Executing query: " + preparedStatement.toString.replaceAll(
      "com.mysql.cj.jdbc.ClientPreparedStatement:", ""
    ), LogLevel.DATABASE)
  }
}
