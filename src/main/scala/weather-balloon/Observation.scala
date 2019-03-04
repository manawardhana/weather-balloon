package weatherballoon.app

import scala.util.Random

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import UnitImplicits._

/**
  * Each weather reading is represented as "Observation" object
  * Celsius and Meter are used as canonical units regardless of the observatory.
  * i.e. Observatory units are converted to canonical units before encapsulation
  */
case class Observation(time: LocalDateTime, location: Location, temperature: Celsius, observatory: String) {
  override def toString():String = {
    Array[String](Observation.formatDateTime(time), location.toString(), temperature.v.toInt.toString, observatory)
      .mkString("|")
  }
}

object Observation {
  /**
    * Parses observation entry (String)
    * @param observation
    * @return
    */
  def apply(observation: String) :Observation = {
    val fields = observation.split("\\|")
    val cordinates = fields(1).split(",")
    val observatory = fields(3)
    Observation (
      time = LocalDateTime.parse(fields(0)),
      location = new Location(inMeters(cordinates(0).toInt, observatory), inMeters(cordinates(1).toInt, observatory)),
      temperature = inCelsius(fields(2).toInt, observatory),
      observatory = observatory
    )
  }

  /**
    * Does the necessary temperature unit conversion (to canonical unit: Celsius) for parsing String records
    * @param t temperature
    * @param observatory
    * @return temperature in Celsius
    */
  def inCelsius(t: Int, observatory: String): Celsius = {
    val unit = if(Config.temperatureUnitByObservatory.exists(_._1 == observatory))
      Config.temperatureUnitByObservatory(observatory)
    else "_"

    unit match {
      case "Kelvin" => kelvinToCelsius(new Kelvin(t))
      case "Celsius" => new Celsius(t)
      case "Fahrenheit" => fahrenheitToCelsius(Celsius(t))
      case _ => kelvinToCelsius(Celsius(t))
    }
  }

  /**
    * Does the necessary distance unit conversion (to canonical unit: Meter) for parsing records
    * @param d
    * @param observatory
    * @return distance in Meter
    */
  def inMeters(d: Int, observatory: String) = {

    val unit = if(Config.distanceUnitByObservatory.exists(_._1 == observatory))
      Config.distanceUnitByObservatory(observatory)
    else "_"
    observatory match {
      case "Meter" => new Meter(d).v.toInt
      case "Kilometer" => kilometerToMeter(new Meter(d)).v.toInt
      case "Mile" => mileToMeter(new Meter(d)).v.toInt
      case _ => kilometerToMeter(new Meter(d)).v.toInt
    }
  }


  /*
   * Random data generation
   */
  val rand = Random

  /**
    * Generates a observation using random values
    * @param valid
    * @return random Observation
    */
  def getSample(valid: Boolean) = {
    Observation(getRandomTime(), getRandomLocation(), getRandomTemperature(), getRandomObservatory())
  }

  /**
    * Generates recent random time
    * @return Random time within last 24 hours (or alike)
    */
  def getRandomTime() = {
    val seconds = LocalDateTime.now.minusDays(1).toEpochSecond(ZoneOffset.UTC) + rand.nextInt(60 * 60 * 24)
    LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC)
  }

  /**
    * Formats date and time as per the specified format
    * @see Config.dateFormat
    * @param time
    * @return formatted date string
    */
  def formatDateTime(time: LocalDateTime): String = {
    val format = DateTimeFormatter.ofPattern(Config.dateFormat)
    time.format(format)
  }

  /**
    * Generates a random location within a specified limit.
    * WARNING: since this function is used to test data generations
    * unit conversion has not been considered.
    * @return a random Location
    */
  def getRandomLocation() = {
    Location(rand.nextInt(100), rand.nextInt(100))
  }

  /**
    * Generates a random temperature within a specified limit.
    * WARNING: since this function is used to test data generations
    * unit conversion has not been considered.
    * @return a random Location
    */
  def getRandomTemperature() = {
    rand.nextInt(150) - 10
  }

  /**
    * Generates random observatory code
    * @return random observatory code as String
    */
   def getRandomObservatory() = {
     val observatories = Array("AU", "US", "FR", "UK", "JP");
     observatories(rand.nextInt(5))
   }
 }
