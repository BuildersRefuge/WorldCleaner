package models

import filesystem.FileSystemManager

class WorldItem(val x: String, val z: String, val playerUUID: String) {
  def getPlayerDestinationPath: String = {
    new FileSystemManager().getOrCreatePlayerFolder(this.playerUUID)
  }
}
