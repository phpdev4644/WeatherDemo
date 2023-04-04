package com.weather.demo.webservice

import com.weather.demo.model.CityResponse
import com.weather.demo.model.DailyForcastResult
import com.weather.demo.utils.Constants
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("locations/v1/cities/search")
    suspend fun city(
        @Query("q") q: String,
        @Query("apikey") apikey: String = Constants.API_KEY,
    ): Response<CityResponse>

    @GET("forecasts/v1/daily/1day/{key}")
    suspend fun weather(
        @Path("key") key: String,
        @Query("details") details: Boolean,
        @Query("metric") metric: Boolean,
        @Query("apikey") apikey: String = Constants.API_KEY
    ): Response<DailyForcastResult>

}
