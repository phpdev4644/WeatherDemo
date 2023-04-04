package com.weather.demo.model

data class UserData(
    val userName: String,
    val description: String,
    val photoUri: String = "",
    val temperature: String = "",
    val wind: String = "",
    val ShortPhrase: String = ""
)
