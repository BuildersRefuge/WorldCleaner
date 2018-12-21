package filesystem

import java.io.{File, IOException}
import java.nio.file.{Files, Path, StandardCopyOption}
import java.text.SimpleDateFormat
import java.util.Date

import config.Configuration
import logging.Logger
import models.Config
import stats.StatsManager

class FileSystemManager {
  /**
    * The file to check for the date restriction.
    */
  val checkFile: String = "level.dat"

  /**
    * Get all directories in the world location directory.
    *
    * @return list of directory paths
    */
  def getDirectoriesInWorldLocation: Array[File] = {
    val config: Config = Configuration.getConfig
    val d = new File(config.folders.worldLocation)
    d.listFiles().filter(p => p.isDirectory)
  }

  /**
    * Get all directory paths for worlds that have not been
    * modified since the match date.
    *
    * @return list of directory paths
    */
  def getDirectoriesMatchingFilter: Array[File] = {
    StatsManager.createCheckpoint("Starting to fetch folders")
    val allDirectories: Array[File] = getDirectoriesInWorldLocation
    val toProcess: Array[File] = Array()
    for (directory <- allDirectories) {
      if (notModifiedSinceMatchDate(directory.getAbsolutePath)) {
        Logger.info(s"${directory.getAbsolutePath} triggers filter")
      }
    }
    StatsManager.createCheckpoint("Completed fetching folders")
    toProcess
  }

  /**
    * Check if a world has not been modified since the match date.
    *
    * @param dir directory path for world
    * @return true if not modified since, else false.
    */
  def notModifiedSinceMatchDate(dir: String): Boolean = {
    // Check if folder contains a level.dat file.
    val levelFile: File = new File(s"$dir/$checkFile")
    if (!levelFile.isFile) {
      Logger.warning(s"No level.dat file found in $dir")
      return false
    }
    val math: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

    // Get the date at last modification.
    val lastModified: Long = levelFile.lastModified()
    val formattedDate = math.format(lastModified)
    val lastModifiedDate: Date = math.parse(formattedDate)

    // Get the filter date from config.
    val modifiedBefore: String = Configuration.getConfig.matchDate
    val modifiedBeforeDate: Date = math.parse(modifiedBefore)

    if (lastModifiedDate.compareTo(modifiedBeforeDate) <= 0) {
      Logger.info(s"World at location <$dir> has not been modified after $modifiedBefore", printToConsole = true)
      return true
    }
    false
  }

  /**
    * Get or create the directory path of player folder.
    *
    * @param uuid uuid of the player
    * @return path of player folder
    */
  def getOrCreatePlayerFolder(uuid: String): String = {
    val parentFolder: String = Configuration.getConfig.folders.disposalLocation
    val targetFolder: File = new File(s"$parentFolder/$checkFile")
    if (!targetFolder.exists()) {
      targetFolder.createNewFile()
    }
    targetFolder.getAbsolutePath
  }

  /**
    * Move a folder between two folders.
    *
    * @param source      path of folder to move
    * @param destination path of where folder should be moved to.
    * @return
    */
  def moveFolder(source: Path, destination: Path): Boolean = {
    try {
      Files.move(source, destination, StandardCopyOption.ATOMIC_MOVE)
      true
    } catch {
      case ioe: IOException =>
        Logger.error("IOException: " + ioe.getMessage)
        false
      case exception: Exception =>
        Logger.error("Exception: " + exception.getMessage)
        false
    }
  }
}
