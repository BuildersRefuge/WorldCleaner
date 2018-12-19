package database

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}

import config.Configuration
import logging.Logger
import models.Config

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
    if(connection == null) this.setConnection()
    val plotTable: String = conf.database.plotTable
    val query: String = s"SELECT owner FROM $plotTable WHERE plot_id_x = ? AND plot_id_z = ? LIMIT 1"
    val statement: PreparedStatement = connection.prepareStatement(query)
    statement.setString(1, x)
    statement.setString(2, z)
    val resultSet: ResultSet = statement.executeQuery()
    resultSet.getString("owner")
  }
}
