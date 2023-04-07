package com.example.weather.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("main")
    @Expose val main: Main? = null,
    @SerializedName("name")
    @Expose val name: String? = null,
    @SerializedName("weather")
    @Expose val weather: List<Weather> = emptyList(),
)

