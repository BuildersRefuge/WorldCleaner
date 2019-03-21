package models

class WorldId(val x: String, val z: String) {
  def this(path: String) {
    this(path.split('/').last.split(',').head, path.split('/').last.split(',').last)
  }

  override def toString: String = {
    "%s,%s".format(x, z)
  }

  def getX: String = x

  def getZ: String = z
}
