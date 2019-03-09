package database

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import config.Configuration
import logging.Logger
import models.Config
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
    val db: String = conf.database.database
    val host: String = conf.database.host
    val port: Int = conf.database.port
    s"jdbc:mysql://$host:$port/$db?verifyServerCertificate=false&useSSL=true"
  }

  /**
    * Get a players UUID from world ids.
    *
    * @param x x coordinate of the world
    * @param z z coordinate of the world
    * @return uuid of the player
    */
  def getPlayerUUIDFromWorld(x: String, z: String): String = {
    if (connection == null) this.setConnection()
    val plotTable: String = conf.database.plotTable
    val query: String = s"SELECT owner FROM $plotTable WHERE plot_id_x = ? AND plot_id_z = ? LIMIT 1"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1, x)
    statement.setString(2, z)
    Logger.info(s"Executing query: $statement.toString")
    val resultSet: ResultSet = statement.executeQuery()
    StatsManager.increaseCounter("sql-queries")
    resultSet.getString("owner")
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
    statement.setString(2, x)
    statement.setString(3, uuid)
    StatsManager.increaseCounter("sql-queries")

    Logger.info(s"Executing query: $statement.toString")
    val result = statement.execute()
    if (!result) Logger.error("Failed to execute last query")
    result
  }
}
