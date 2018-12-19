package models

import filesystem.FileSystemManager

class WorldItem(val x: Int, val z: Int, val player: Player) {
  def getPlayerDestinationPath: String = {
    new FileSystemManager().getOrCreatePlayerFolder(this.player.uuid)
  }
}
