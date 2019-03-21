import java.io.File

import database.DatabaseManager
import filesystem.FileSystemManager
import logging.{LogLevel, Logger}
import models.{WorldId, WorldItem}
import stats.StatsManager

object CleanUpManager {
  def run(): Unit = {
    val fsm: FileSystemManager = new FileSystemManager()
    val dbm: DatabaseManager = new DatabaseManager()

    val toRemove: Array[File] = fsm.getDirectoriesMatchingFilter

    for (world <- toRemove) {
      try {
        StatsManager.increaseCounter("worlds-processed")
        Logger.info(s"Running for <$world>")

        // Get the id from the world folder
        val id: WorldId = new WorldId(world.getAbsolutePath)
        Logger.printToOnlyConsole("Processing world: " + id.toString, LogLevel.INFO)

        // Get the owner uuid connected with that world id
        val ownerUUID: String = dbm.getPlayerUUIDFromWorld(id.getX, id.getZ)
        if (ownerUUID == null) {
          Logger.error(s"Failed to fetch player details for world ${id.toString}")
          Logger.printToOnlyConsole("Failed to process world: " + id.toString + "  (Invalid owner)", LogLevel.ERROR)
          StatsManager.increaseCounter("worlds-failed")
        } else {
          // Create a wrapper around the world
          val worldItem: WorldItem = new WorldItem(id, ownerUUID)
          val destinationLocation: File = new File(worldItem.getPlayerDestinationPath + "/" + id.toString)
          Logger.printToOnlyConsole("Starting to move world: " + id.toString, LogLevel.INFO)

          // Move the world folder from its original location to the disposal location
          if (fsm.moveFolder(world.toPath, destinationLocation.toPath)) {
            Logger.info(s"Moved folder: Source <${world.getAbsolutePath}> Destination: <${destinationLocation.getAbsolutePath}>")
            Logger.printToOnlyConsole("World " + id.toString + " successfully moved", LogLevel.INFO)
            StatsManager.increaseCounter("worlds-moved")

            // Delete the entry from the database so the world can be reused
            if (!dbm.deletePlotEntry(worldItem.id.getX, worldItem.id.getZ, worldItem.playerUUID)) {
              Logger.error(s"Failed to delete database entry for ${id.toString}")
              Logger.printToOnlyConsole("Failed to delete database entry for world " + id.toString + " (Folder still moved)", LogLevel.WARNING)
            }
          } else {
            // Folder failed to move, or parts of it could have been moved
            Logger.info(s"Failed to move folder: Source <${world.getAbsolutePath}> Destination: <${destinationLocation.getAbsolutePath}>")
            Logger.printToOnlyConsole("Failed to process world " + id.toString, LogLevel.ERROR)
            StatsManager.increaseCounter("worlds-failed")
          }
        }
      } catch {
        case e: Exception =>
          Logger.error(s"Exception for folder: Source <${world.getAbsolutePath}>", printToConsole = true)
          Logger.error(e.toString)
      }
    }
  }
}
