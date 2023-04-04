package com.weather.demo.model

class ErrorResponse : ArrayList<ErrorResponseItem>()

data class ErrorResponseItem(
    val field: String = "",
    val name: String = "",
    val message: String = ""
)


data class ErrorObject(
    val code: Int,
    val message: String,
    val name: String,
    val status: Int
)