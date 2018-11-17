import config.Configuration
import logging.Logger
import models.Config

object Main  {

  def main(args: Array[String]): Unit = {
    Logger.info("WorldCleaner starting")
    val config: Config = Configuration.getConfig


    Logger.info("WorldCleaner stopping")
  }
}
