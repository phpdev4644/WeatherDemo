package com.weather.demo.model

data class DailyForcastResult(
    val DailyForecasts: List<DailyForecast>,
    val Headline: Headline
)

data class Day(
    val CloudCover: Int,
    val Evapotranspiration: Evapotranspiration,
    val HasPrecipitation: Boolean,
    val HoursOfIce: Int,
    val HoursOfPrecipitation: Int,
    val HoursOfRain: Int,
    val HoursOfSnow: Int,
    val Ice: Ice,
    val IceProbability: Int,
    val Icon: Int,
    val IconPhrase: String,
    val LongPhrase: String,
    val PrecipitationProbability: Int,
    val Rain: Rain,
    val RainProbability: Int,
    val ShortPhrase: String,
    val Snow: Snow,
    val SnowProbability: Int,
    val SolarIrradiance: SolarIrradiance,
    val ThunderstormProbability: Int,
    val TotalLiquid: TotalLiquid,
    val Wind: Wind,
    val WindGust: WindGust
)

data class DailyForecast(
    val AirAndPollen: List<AirAndPollen>,
    val Date: String,
    val Day: Day,
    val DegreeDaySummary: DegreeDaySummary,
    val EpochDate: Int,
    val HoursOfSun: Double,
    val Link: String,
    val MobileLink: String,
    val Moon: Moon,
    val Night: Night,
    val RealFeelTemperature: RealFeelTemperature,
    val RealFeelTemperatureShade: RealFeelTemperatureShade,
    val Sources: List<String>,
    val Sun: Sun,
    val Temperature: Temperature
)

data class Rain(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Headline(
    val Category: String,
    val EffectiveDate: String,
    val EffectiveEpochDate: Int,
    val EndDate: String,
    val EndEpochDate: Int,
    val Link: String,
    val MobileLink: String,
    val Severity: Int,
    val Text: String
)

data class Maximum(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Minimum(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Night(
    val HasPrecipitation: Boolean,
    val Icon: Int,
    val IconPhrase: String
)

data class Temperature(
    val Maximum: Maximum,
    val Minimum: Minimum
)

data class Wind(
    val Direction: Direction,
    val Speed: Speed
)

data class WindGust(
    val Direction: Direction,
    val Speed: Speed
)

data class TotalLiquid(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class RealFeelTemperature(
    val Maximum: Maximum,
    val Minimum: Minimum
)

data class RealFeelTemperatureShade(
    val Maximum: Maximum,
    val Minimum: Minimum
)

data class Snow(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class SolarIrradiance(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Speed(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Sun(
    val EpochRise: Int,
    val EpochSet: Int,
    val Rise: String,
    val Set: String
)
data class Evapotranspiration(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)
data class Heating(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Ice(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class Moon(
    val Age: Int,
    val EpochRise: Int,
    val EpochSet: Any,
    val Phase: String,
    val Rise: String,
    val Set: Any
)

data class AirAndPollen(
    val Category: String,
    val CategoryValue: Any,
    val Name: String,
    val Type: String,
    val Value: Any
)

data class Cooling(
    val Unit: String,
    val UnitType: Int,
    val Value: Any
)

data class DegreeDaySummary(
    val Cooling: Cooling,
    val Heating: Heating
)

data class Direction(
    val Degrees: Any,
    val English: String,
    val Localized: String
)