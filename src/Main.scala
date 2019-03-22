import config.Configuration
import database.DatabaseManager
import logging.Logger
import models.Config
import stats.RuntimeStatsManager

object Main {

  def main(args: Array[String]): Unit = {
    RuntimeStatsManager.start()
    Logger.info("WorldCleaner starting", printToConsole = true)
    try {
      Logger.info("Doing initial checks", printToConsole = true)
      Logger.info("Trying to load config", printToConsole = true)
      val config: Config = Configuration.getConfig
      if (config == null) {
        System.out.println("Please update world-cleaner.json with your configuration and restart WorldCleaner")
        return
      }
      Logger.info("Config loaded successfully", printToConsole = true)
      Logger.info("Trying to connect to database", printToConsole = true)
      new DatabaseManager().setConnection()
      Logger.info("Connected to database", printToConsole = true)
      Logger.info("Initial checks passed", printToConsole = true)
      Logger.info(s"World location set to: ${config.folders.worldLocation}", printToConsole = true)
      Logger.info(s"Disposal location set to: ${config.folders.disposalLocation}", printToConsole = true)
      Logger.info("Creating stats counters", printToConsole = true)
      RuntimeStatsManager.addCounter("sql-queries")
      RuntimeStatsManager.addCounter("worlds-processed")
      RuntimeStatsManager.addCounter("worlds-moved")
      RuntimeStatsManager.addCounter("worlds-failed")
      Logger.info("Done creating stats counters", printToConsole = true)
      Logger.info("Starting cleaning process", printToConsole = true)
      CleanUpManager.run
      RuntimeStatsManager.stop()
      val stats: String = RuntimeStatsManager.toString
      Logger.info(stats, printToConsole = true)
    } catch {
      case e: Exception =>
        Logger.error("Initial checks failed, see logfile for more info", printToConsole = true)
        Logger.error(e.toString)
    }
    Logger.info("WorldCleaner stopping", printToConsole = true)
  }
}
