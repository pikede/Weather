package com.example.weather.network

import com.example.weather.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// hard coded can be stored in resource folder
const val APP_ID = "14db5c67db1c7853d46d830e75486525"

interface WeatherApi {

    // appends appid, needed for network call by default
    @GET("weather")
    suspend fun getCityWeatherCityName(
        @Query(value = "q") cityName: String,
        @Query(value = "APPID") appid: String = APP_ID
    ): Response<WeatherResponse>

    // appends appid, needed for network call by default
    @GET("weather")
    suspend fun getCityByCoordinatesWeather(
        @Query(value = "lat") lat: Double,
        @Query(value = "lon") lon: Double,
        @Query(value = "APPID") appid: String = APP_ID
    ): Response<WeatherResponse>
}