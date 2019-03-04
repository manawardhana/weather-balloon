package weatherballoon.app

/**
  * Application configuration values
  */
object Config {
  //Dateformat used to parsing and writing
  val dateFormat = "yyyy-MM-dd'T'HH:mm"

  //File used to generate data and serve as the source for formalizing data
  val primaryFileName = "observations.dat"

  //Output file for formalized data
  val formalFileName = "formalized-observations.dat"

  //Observatory unit mapping - temperature
  val temperatureUnitByObservatory = Map("AU" -> "Celsius", "US" -> "Fahrenheit", "FR" -> "Kelvin", "_" -> "Kelvin")

  //Observatory unit mapping - distance
  val distanceUnitByObservatory = Map("AU" -> "Kilometer", "US" -> "Mile", "FR" -> "Meter", "_" -> "Kilometer")


  val menuText = """"

   ==== WEATHER OBSERVATIONS ====

   Please Select an Action:
   ------------------------

    1) Display MINimum temperature
    2) Display MAXimum temperature
    3) Display MEAN tempurature
    4) The NUMBER OF OBSERVATIONS from each observatory
    5) TOTAL DISTANCE travelled
    6) Generate sample data set
    7) Formalize file data
    8) Quit
    """

}
