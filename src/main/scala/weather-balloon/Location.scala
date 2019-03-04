package weatherballoon.app

/**
  * Location Class and Object
  * @param x cordinate x
  * @param y cordinate y
  */
case class Location(x: Int, y: Int) {
  override def toString = {
    this.x + "," + this.y
  }
}

object Location{
  def apply(location :String): Location = {
    val cordinates = location.split(",")
    Location(cordinates(0).toInt, cordinates(1).toInt)
  }
}
