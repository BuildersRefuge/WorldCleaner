package database

import java.sql.{Connection, DriverManager, ResultSet}

import config.Configuration
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
  private def getConnectionString: String = s"jdbc:mysql://${conf.database.host}:${conf.database.port}/${conf.database.database}?verifyServerCertificate=false&useSSL=true"

  def performInsert(query: String): Boolean = {
   // TODO: Create
  }

  def performSelect(query : String) : ResultSet = {
    // TODO: Create
  }

  def performDelete(query : String) : Boolean = {
    // TODO: Create
  }

}
