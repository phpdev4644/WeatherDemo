package com.weather.demo.viewmodel

import com.weather.demo.webservice.ApiClient
import com.weather.demo.Controller
import com.weather.demo.model.WeatherData

class CityViewModel : BaseViewModel() {

    fun cityApi(
        city: String,
        response: (WeatherData) -> Unit,
        error: (String) -> Unit = {}
    ) {

        callApiAndShowDialog(call = {
            ApiClient.getClient().city(city)
        }, handleSuccess = { data ->
            weatherApi(data[0].Key, {
                response.invoke(it)
            })
        }, handleGeneric = {
            error.invoke(it)
            Controller.instance.context?.hideProgress()
        }, showDialg = false)
    }

    private fun weatherApi(key: String, response: (WeatherData) -> Unit, error: () -> Unit = {}) {

        callApiAndShowDialog(call = {
            ApiClient.getClient().weather(
                key, true,
                metric = true
            )
        }, handleSuccess = {

            val data = it.DailyForecasts[0]
            val temp = data.Temperature.Maximum.Value.toString() + "\u00B0c"
            val wind =
                data.Day.Wind.Speed.Value.toString() + " " + it.DailyForecasts[0].Day.Wind.Speed.Unit
            val sunshine = data.Day.ShortPhrase

            val weatherData = WeatherData(temp, wind, sunshine)
            response.invoke(weatherData)

        }, handleGeneric = {
            error.invoke()
            Controller.instance.context?.hideProgress()
        }, showDialg = false)
    }

}