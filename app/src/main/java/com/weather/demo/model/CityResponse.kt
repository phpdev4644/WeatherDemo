package com.weather.demo.model

class CityResponse : ArrayList<CityResponseItem>()

data class CityResponseItem(
    val AdministrativeArea: AdministrativeArea,
    val Country: Country,
    val DataSets: List<String>,
    val EnglishName: String,
    val GeoPosition: GeoPosition,
    val IsAlias: Boolean,
    val Key: String,
    val LocalizedName: String,
    val PrimaryPostalCode: String,
    val Rank: Int,
    val Region: Region,
    val SupplementalAdminAreas: List<SupplementalAdminArea>,
    val TimeZone: TimeZone,
    val Type: String,
    val Version: Int
)

data class AdministrativeArea(
    val CountryID: String,
    val EnglishName: String,
    val EnglishType: String,
    val ID: String,
    val Level: Int,
    val LocalizedName: String,
    val LocalizedType: String
)

data class Country(
    val EnglishName: String,
    val ID: String,
    val LocalizedName: String
)

data class GeoPosition(
    val Elevation: Elevation,
    val Latitude: Double,
    val Longitude: Double
)

data class Region(
    val EnglishName: String,
    val ID: String,
    val LocalizedName: String
)

data class SupplementalAdminArea(
    val EnglishName: String,
    val Level: Int,
    val LocalizedName: String
)

data class TimeZone(
    val Code: String,
    val GmtOffset: Double,
    val IsDaylightSaving: Boolean,
    val Name: String,
    val NextOffsetChange: Any
)

data class Elevation(
    val Imperial: Imperial,
    val Metric: Metric
)

data class Imperial(
    val Unit: String,
    val UnitType: Int,
    val Value: Double
)

data class Metric(
    val Unit: String,
    val UnitType: Int,
    val Value: Double
)