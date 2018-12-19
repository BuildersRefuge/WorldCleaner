package config

import com.google.gson.{Gson, GsonBuilder}
import java.io._

import logging.Logger
import models.{Config, DatabaseConfig, FolderConfig}

import scala.io.Source

/**
  * Main configuration handler.
  */
object Configuration {
  var config: Config = _
  val configName: String = "world-cleaner.json"

  /**
    * Get the current configuration instance.
    * If no configuration is loaded, existing will be loaded
    * or a new will be created
    *
    * @return The current configuration instance.
    */
  def getConfig: Config = {
    try {
      if (config == null) {
        Logger.debug("Loading config instance")
        if (new File(configName).exists()) {
          config = loadConfig
          return config
        }
        else {
          Logger.debug("Creating config")
          createConfig()
          return null
        }
      }
      return config
    }
    catch {
      case ioe: IOException => Logger.error("IOException: " + ioe.getMessage)
      case exception: Exception => Logger.error("Exception: " + exception.getMessage)
    }
    null
  }

  /**
    * Load configuration from file.
    *
    * @return instance of config from file
    */
  private def loadConfig: Config = {
    val gson: Gson = getGsonBuilder
    val content: String = getConfigFileContents
    gson.fromJson(content, classOf[Config])
  }

  /**
    * Create a new configuration file with default values.
    */
  private def createConfig(): Unit = {
    val gson: Gson = getGsonBuilder
    val out = new BufferedWriter(new FileWriter(new File(configName)))
    out.write(gson.toJson(getEmptyConfig))
    out.close()
  }

  /**
    * Get a Gson object from builder.
    *
    * @return Gson object
    */
  private def getGsonBuilder: Gson = {
    val gsonBuilder: GsonBuilder = new GsonBuilder
    gsonBuilder.serializeNulls
    gsonBuilder.setPrettyPrinting()
    gsonBuilder.create
  }

  /**
    * Get a empty Config object with default values.
    *
    * @return
    */
  private def getEmptyConfig: Config = {
    val cfg = new Config()
    cfg.database = new DatabaseConfig
    cfg.folders = new FolderConfig
    cfg
  }

  /**
    * Load content in file.
    *
    * @return content of file
    */
  private def getConfigFileContents: String = Source.fromFile(configName).getLines().mkString
}
