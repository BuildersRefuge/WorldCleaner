package filesystem

import java.io.{File, IOException}
import java.nio.file.{Files, Path, StandardCopyOption}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import config.Configuration
import logging.Logger
import models.Config
import org.apache.commons.io.FileUtils

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
    val allWorlds: Array[File] = getDirectoriesInWorldLocation
    var unmodifiedWorlds: Array[File] = Array()
    for (world <- allWorlds) {
      if (!modifiedSinceMatchDate(world.getAbsolutePath)) {
        Logger.info(s"${world.getAbsolutePath} triggers filter")
        unmodifiedWorlds :+= world
      }
    }
    unmodifiedWorlds
  }

  /**
    * Check if a world has not been modified since the match date.
    *
    * @param dir directory path for world
    * @return true if modified since, else false.
    */
  def modifiedSinceMatchDate(dir: String): Boolean = {
    // Check if folder contains a level.dat file.
    val levelFile: File = FileUtils.getFile(s"$dir/$checkFile")
    if (!levelFile.isFile) {
      Logger.warning(s"No level.dat file found in $dir")
      return false
    }
    val math: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")

    // Get the filter date from config.
    val modifiedBefore: String = Configuration.getConfig.matchDate
    val modifiedBeforeDate: Date = math.parse(modifiedBefore)

    if (!FileUtils.isFileOlder(levelFile, modifiedBeforeDate)) {
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
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.format(Calendar.getInstance().getTime)
    val parentFolder: String = Configuration.getConfig.folders.disposalLocation
    val targetFolder: File = FileUtils.getFile(s"$parentFolder/$date/$uuid")

    if (!targetFolder.exists()) {
      targetFolder.mkdirs()
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
  def moveFolder(source: File, destination: File): Boolean = {
    try {
      FileUtils.moveDirectory(source, destination)
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
