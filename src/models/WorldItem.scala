package models

import filesystem.FileSystemManager

class WorldItem(val x: String, val z: String, val player: Player) {
  def getPlayerDestinationPath: String = {
    new FileSystemManager().getOrCreatePlayerFolder(this.player.uuid)
  }
}
