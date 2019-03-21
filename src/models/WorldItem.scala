package models

import filesystem.FileSystemManager

class WorldItem(val id: WorldId, val playerUUID: String) {
  def getPlayerDestinationPath: String = {
    new FileSystemManager().getOrCreatePlayerFolder(this.playerUUID)
  }
}
