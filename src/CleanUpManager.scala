import java.io.File

import database.DatabaseManager
import filesystem.FileSystemManager
import logging.Logger
import models.WorldItem
import stats.StatsManager

object CleanUpManager {
  def run: Unit = {
    val fsm: FileSystemManager = new FileSystemManager()
    val dbm: DatabaseManager = new DatabaseManager()

    val toRemove: Array[File] = fsm.getDirectoriesMatchingFilter
    StatsManager.createCheckpoint("Starting to process folders")
    for (folder <- toRemove) {
      StatsManager.increaseCounter("worlds-processed")
      Logger.info(s"Running for <$folder>")
      val id: (String, String) = parseIdsFromPath(folder.getAbsolutePath)
      val ownerUUID: String = dbm.getPlayerUUIDFromWorld(id._1, id._2)
      if (ownerUUID == null) {
        Logger.error(s"Failed to fetch player details for world ${id.toString()}")
        StatsManager.increaseCounter("worlds-failed")
      } else {
        val worldItem: WorldItem = new WorldItem(id._1, id._2, ownerUUID)
        val destinationLocation: File = new File(worldItem.getPlayerDestinationPath)
        if (fsm.moveFolder(folder.toPath, destinationLocation.toPath)) {
          Logger.info(s"Moved folder: Source <${folder.getAbsolutePath}> Destination: <${destinationLocation.getAbsolutePath}>")
          StatsManager.increaseCounter("worlds-moved")
          if (!dbm.deletePlotEntry(worldItem.x, worldItem.z, worldItem.playerUUID)) {
            Logger.error(s"Failed to delete database entry for ${worldItem.x};${worldItem.z}")
          }
        } else {
          Logger.info(s"Failed to move folder: Source <${folder.getAbsolutePath}> Destination: <${destinationLocation.getAbsolutePath}>")
          StatsManager.increaseCounter("worlds-failed")
        }
      }
    }
    StatsManager.createCheckpoint("Done processing folders")
  }

  def parseIdsFromPath(path: String): (String, String) = {
    val lastPathSegment = path.split('/').last
    val idSegments = lastPathSegment.split(',')
    (idSegments.head, idSegments.last)
  }
}
