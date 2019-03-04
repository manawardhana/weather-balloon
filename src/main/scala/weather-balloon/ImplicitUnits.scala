package weatherballoon.app

package object UnitImplicits {

  implicit class Meter (val v: Double) {
    override def toString() = v + " Meter"
  }

  implicit class Kilometer (val v: Double) {
    override def toString() = v + " Kilometer"
  }

  implicit class Mile (val v: Double) { 
    override def toString() = v + " Mile"
  }

  implicit class Celsius (val v: Double) {
    override def toString() = v + " degrees Celsius"
  }
  
  implicit class Kelvin (val v: Double) {
    override def toString() =  v + " degrees Kelvin"
  }

  implicit class Fahrenheit (val v: Double) {
    override def toString() =  v + " degrees Fahrenheit" 
  }
  
  //Ordering
  implicit object CelsiusOrdering extends Ordering[Celsius] {
    def compare (a: Celsius, b: Celsius) = a.v compare b.v
  }

  implicit object ObservationOrderingByTemperature extends Ordering[Observation] {
    def compare (a: Observation, b: Observation) = a.temperature.v compare b.temperature.v
  }

  //Implicit Unit Conversions
  implicit def celsiusToFahrenheit(c: Celsius): Fahrenheit = {
    Fahrenheit(((c.v * 9)/5) + 32)
  }
  
  implicit def fahrenheitToCelsius(f: Fahrenheit): Celsius = {
    Celsius(((f.v - 32) * 5) / 9)
  }

  implicit def celsiusToKelvin(c: Celsius): Kelvin = {
    Kelvin(c.v + 273.5)
  }

  implicit def kelvinToCelsius(k: Kelvin): Celsius = {
    Celsius(k.v - 273.5) 
  }

  implicit def kilometerToMeter(km: Kilometer): Meter = {
    Meter(km.v * 1000)
  }

  implicit def meterToKilometer(m: Meter): Kilometer = {
    Kilometer(m.v / 1000)
  }

  implicit def mileToMeter(m: Mile): Meter = {
    Meter(m.v * 1609.34)
  }

  implicit def meterToMile(m: Meter): Mile = {
    Mile(m.v / 1609.34)
  }
}

