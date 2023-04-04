package com.weather.demo.model

data class ErrorResponseCity(
    val Code: String,
    val Message: String = "",
    val Reference: String
)