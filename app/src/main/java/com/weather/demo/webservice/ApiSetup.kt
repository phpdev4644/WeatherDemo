package com.weather.demo.webservice


import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.weather.demo.model.ErrorResponse
import com.weather.demo.model.ErrorResponseCity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class NoContent(val error: String? = null) : ResultWrapper<Nothing>()
    data class GenericError(val error: String? = null) : ResultWrapper<Nothing>()
    data class ServerError(val error: String? = null) : ResultWrapper<Nothing>()
    data class NetworkError(val error: String?) : ResultWrapper<Nothing>()
    data class SessionExpiredError(val error: String?) : ResultWrapper<Nothing>()
}

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> Response<T>
): ResultWrapper<Response<T>> {
    return withContext(dispatcher) {
        try {
            val result = apiCall.invoke()
            /* if (BuildConfig.DEBUG) {*/
            Log.e("Rk", Gson().toJson(result.body()))
            /*   }*/

            when (result.code()) {
                201 -> {
                    // success
                    ResultWrapper.Success(result)
                }
                200 -> {
                    if (result.body() != null) {
                        ResultWrapper.Success(result)
                    } else {
                        ResultWrapper.NoContent(null)
                    }
                }
                204 -> {
                    // No Content
                    ResultWrapper.NoContent(null)
                }
                401 -> {
                    ResultWrapper.SessionExpiredError("")
                }
                422 -> {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponse>() {}.type
                    val errorBody: ErrorResponse? =
                        gson.fromJson(result.errorBody()!!.charStream(), type)

                    ResultWrapper.GenericError(errorBody!![0].message.toString())
                }

                // 400,422,404,504
                else -> {
                    val gson = Gson()
                    val type = object : TypeToken<ErrorResponseCity>() {}.type
                    val errorBody: ErrorResponseCity? =
                        gson.fromJson(result.errorBody()!!.charStream(), type)

                    if (errorBody != null) {
                        if (errorBody.Message != "") {
                            ResultWrapper.GenericError(errorBody.Message.toString())
                        } else {
                            ResultWrapper.GenericError(errorBody.toString())
                        }

                    } else {
                        ResultWrapper.GenericError(null)
                    }
                }
            }

        } catch (throwable: Throwable) {
            Log.e("Rk", throwable.message.toString())
            ResultWrapper.NetworkError(throwable.message)
        }
    }
}

