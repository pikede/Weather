package com.example.weather.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Main(
    @SerializedName("temp")
    @Expose
    val temp: Double? = null,
    @SerializedName("feels_like")
    @Expose
    val feels_like: Double? = null,
    @SerializedName("temp_min")
    @Expose
    val temp_min: Double? = null,
    @SerializedName("temp_max")
    @Expose
    val temp_max: Double? = null,
    @SerializedName("pressure")
    @Expose
    val pressure: Int? = null,
    @SerializedName("humidity")
    @Expose
    val humidity: Double? = null,
)
