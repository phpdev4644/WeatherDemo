package com.weather.demo.webservice

import com.weather.demo.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val readTimeOut: Long = 50
    private const val writeTimeOut: Long = 60
    private const val connectionTimeOut: Long = 50

    fun getClient(): ApiService {


        val okHttpClient = OkHttpClient.Builder().run {
            // debuge
            if (BuildConfig.DEBUG) {
                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(logging)
            }

            addInterceptor(Interceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                requestBuilder.addHeader("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            })
            connectTimeout(connectionTimeOut, TimeUnit.SECONDS)
            readTimeout(readTimeOut, TimeUnit.SECONDS)
            writeTimeout(writeTimeOut, TimeUnit.SECONDS)
            build()

        }

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://dataservice.accuweather.com/")
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }


}