/*
 * TODO:
 * Error handling
 * duplicates
 * closing files
 * streems
 * can the samples generated via implicits
 * README
 */
package weatherballoon.app

import scala.io.Source._
import scala.io.BufferedSource
import scala.io.StdIn

import UnitImplicits._

object WeatherBalloonApp {

  def main(args: Array[String]) = {

    var action = readUserAction()
    while (action != 8) {

      def file = fromFile(Config.primaryFileName)

      action match {
        case 0 => {
          displayMenu()
        }
        case 1 => {
          println(s"Reported maximum temperature is ${ObservationFile.getMaxTemperature(file)}")
        }
        case 2 => {
          println(s"Reported minimum temperature is ${ObservationFile.getMinTemperature(file)}")
        }
        case 3 => {
          println(s"Reported mean temperature is ${ObservationFile.getMeanTemperature(file)}")
        }
        case 4 => {
          ObservationFile.printObservatoryStats(file)
        }
        case 5 => {
          println("Sorry, This feature is pending")
        }
        case 6 => {
          println("Enter the number of entries:");
          ObservationFile.generateSampleFile(StdIn.readInt())
        }
        case 7 => {
          println("Formalizing file data...."); ObservationFile.writeToFormalFile(file)
        }
        case _ => {
          println("Invalid Input")
        }
      }
      action = readUserAction()
    }
  }

  def displayMenu(): Unit = {
    println("Press <Enter> to display the menu");
    StdIn.readLine
    println(Config.menuText)
  }

  def readUserAction(): Int = {
    displayMenu()
    StdIn.readInt()
  }
}

object ObservationFile {

  val distanceUnits = Array[(Int, String)](
    (1, "Meter"),
    (2, "Kilometer"),
    (3, "Mile")
  )

  val temperatureUnits = Array[(Int, String)](
    (1, "Celsius"),
    (2, "Kelvin"),
    (3, "Fahrenheit")
  )

  /**
    * Calculates maximum temperature for the observations in the given file
    * @param fileSource
    * @return max temperature in Celsius
    */
  def getMaxTemperature(fileSource: BufferedSource) = {
    fileSource.getLines.map(Observation.apply).reduceLeft{
      ObservationOrderingByTemperature.max(_, _)
    }.temperature
  }

  /**
    * Calculates minimum temperature for observations in the given file
    * @param fileSource
    * @return minimum temperature in Celsius
    */
  def getMinTemperature(fileSource: BufferedSource) = {
    fileSource.getLines.map(Observation.apply).reduceLeft{
      ObservationOrderingByTemperature.min(_, _)
    }.temperature
  }

  /**
    * Calculates mean temperature for all the observations in the given file
    * @param fileSource
    * @return mean temperature in Celsius
    */
  def getMeanTemperature(fileSource: BufferedSource) = {
    val (totalTemperature, count) = fileSource.getLines.map(Observation.apply).foldLeft((0.0, 0)){
      (a, o) => (a._1 + o.temperature.v, a._2 + 1)
    }
    Celsius(totalTemperature/count)
  }

  /**
    * Calculates observation count by observatory
    * @param fileSource
    * @return
    */
  def getObservatoryStats(fileSource: BufferedSource) = {
    fileSource.getLines.map(Observation.apply)
      .foldLeft(Map[String, Int]())((m, v) => {
        m + (v.observatory ->
          (if (m.keys.exists(_ == v.observatory))
            (m(v.observatory) + 1)
           else 1))
      })
  }

  /**
    * Displays the count by each observatory
    * @param fileSource
    */
  def printObservatoryStats(fileSource: BufferedSource): Unit = {
    println("==== Observatory Statistics ====")
    getObservatoryStats(fileSource).foreach((v) => println(s"${v._1} -> ${v._2}"))
  }

  /**
    * Generates prmary file with sample data of length of users choice
    * @param recordCount
    */
  def generateSampleFile(recordCount: Int): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(Config.primaryFileName))

    var i = 0
    for (i <- (1 to recordCount)) {
      pw.write(Observation.getSample(true).toString + "\n")

      if ((i % 100000) == 0) {
        println(s"wrote ${i}th record")
      }
    }
    pw.close
  }

  /**
    * Prompts user for a single unit selection
    * @param units
    * @return
    */
  def readUserPreferedUnit(units: Array[(Int, String)]) = {
    println("Select prefered unit...")
    units.foreach(u => println(s"${u._1} -> ${u._2}"))

    val input = StdIn.readInt()
    units(input - 1)._2
  }

  /**
    * Prompts user for preferred unit selections
    * @return
    */
  def readUserPreferedUnits(): (String, String) = {
    (readUserPreferedUnit(distanceUnits), readUserPreferedUnit(temperatureUnits))
  }

  /**
    * Allows user to select preferred temperature unit and distance unit
    * Formalizes and writes all the records to a new file from primary file
    * @param fileSource
    */
  def writeToFormalFile(fileSource: BufferedSource) = {
    val units = readUserPreferedUnits()

    import java.io._
    val pw = new PrintWriter(new File(Config.formalFileName))
    fileSource.getLines.map(Observation.apply).foreach{ o =>
      pw.write(Array[String](
        Observation.formatDateTime(o.time),
        inDistanceUnit(o.location, units._1),
        inTemperatureUnit(o.temperature, units._2).toString,
        o.observatory).mkString("|") + "\n")
    }
    pw.close
  }

  /**
    * Does the necessary temperature unit conversion for formalizing records
    * @param c
    * @param unit
    * @return
    */
  def inTemperatureUnit(c: Celsius, unit: String) = {
    val x = unit match {
      case "Kelvin" => celsiusToKelvin(c).v
      case "Celsius" => c.v
      case "Fahrenheit" => celsiusToFahrenheit(c).v
    }

    x.toInt
  }

  /**
    * Does the necessary distance unit conversion for formalizing records
    * @param l
    * @param unit
    * @return
    */
  def inDistanceUnit(l: Location, unit: String) = {

    unit match {
      case "Meter" => Array(l.x, l.y).mkString(",")
      case "Kilometer" => Array(meterToKilometer(l.x).v, meterToKilometer(l.y).v).mkString(",")
      case "Mile" => Array(meterToMile(l.x).v, meterToMile(l.y).v).mkString(",")
    }
  }
 }
