package database

import java.sql.{Connection, DriverManager, ResultSet}

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
  def DatabaseManager() {
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

  def performInsert(query: String): Boolean = {
    Logger.info(s"Executing query: $query")
    // TODO: Create
    false
  }

  def performSelect(query: String): ResultSet = {
    Logger.info(s"Executing query: $query")
    // TODO: Create
    null
  }

  def performDelete(query: String): Boolean = {
    Logger.info(s"Executing query: $query")
    // TODO: Create
    false
  }

}
