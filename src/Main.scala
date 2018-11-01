import config.Configuration
import logging.Logger
import models.Config

object Main extends App {

  def run(): Unit = {
    Logger.info("WorldCleaner starting")
    val config: Config = Configuration.getConfig

    Logger.info("WorldCleaner stopping")
  }

  run()
}
