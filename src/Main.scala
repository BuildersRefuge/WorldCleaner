import config.Configuration
import logging.Logger
import models.Config
import stats.StatsManager

object Main {

  def main(args: Array[String]): Unit = {
    StatsManager.start()
    Logger.info("WorldCleaner starting")
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
      //new DatabaseManager().setConnection()
      Logger.info("Connected to database", printToConsole = true)
      Logger.info("Initial checks passed", printToConsole = true)
      Logger.info(s"World location set to: ${config.folders.worldLocation}", printToConsole = true)
      Logger.info(s"Disposal location set to: ${config.folders.disposalLocation}", printToConsole = true)
      Logger.info("Creating counters", printToConsole = true)
      StatsManager.addCounter("sql-queries")
      StatsManager.addCounter("worlds-processed")
      StatsManager.addCounter("worlds-moved")
      StatsManager.addCounter("worlds-failed")
      Logger.info("Done creating counters", printToConsole = true)
      StatsManager.createCheckpoint("Initialization completed")
      Logger.info("Starting cleaning process")
      CleanUpManager.run
      StatsManager.stop()
      val stats: String = StatsManager.toString
      Logger.info(stats, printToConsole = true)
    } catch {
      case e: Exception =>
        Logger.error("Initial checks failed, see logfile for more info", printToConsole = true)
        Logger.error(e.toString)
    }
    Logger.info("WorldCleaner stopping")
  }
}
