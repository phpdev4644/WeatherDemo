package com.weather.demo.model

import com.google.gson.annotations.SerializedName

open class ObjectBaseModel<T>(@SerializedName("data") var data: T?) :
    BaseModel(0, false, "Something went wrong") {
    class Error<T>(errorCode: Int, errorMessage: String, data: T?) : ObjectBaseModel<T>(data) {
        init {
            message = errorMessage
            code = errorCode
        }
    }
}

open class BaseModel(
    @SerializedName("status") var code: Int = 200,
    @SerializedName("success") var success: Boolean = false,
    @SerializedName("message") var message: String = ""
) {
}