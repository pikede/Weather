package com.example.weather

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather.models.WeatherResponse
import com.example.weather.network.WeatherService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response


class MainViewModel(application: Application) : AndroidViewModel(application) {
    // TODO  given more time I would prefer to add a repository and move api calls to repository
    private val api by lazy { WeatherService().api }

    private val _weatherDetails = MutableLiveData<WeatherResponse>()
    val weatherDetails: LiveData<WeatherResponse> get() = _weatherDetails
    private val _errorMsg = MutableLiveData<String>()
    val errorMsg: LiveData<String> get() = _errorMsg

    fun getCityData(cityName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val temp = cityName.lowercase().trim()
                // uses only city name to get weather details
                handleWeatherResponse(api.getCityWeatherCityName(temp))
            } catch (e: Exception) {
                // logs exception, if any
                logError(e.message ?: e.toString())
            }
        }
    }

    fun getCityDataByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                handleWeatherResponse(api.getCityByCoordinatesWeather(lat, lon))
            } catch (e: Exception) {
                // logs exception, if any
                logError(e.message ?: e.toString())
            }
        }
    }

    private fun handleWeatherResponse(response: Response<WeatherResponse>) {
        // success path
        if (response.isSuccessful) {
            Log.d("**logged", "successfully retrieved weather response")
            response.body()?.let {
                _weatherDetails.postValue(it)
            }
        } else {
            // logs network failure
            try {
                val gson = Gson()
                val errorObj = gson.fromJson(response.body().toString(), WeatherResponse::class.java)
                logError(errorObj.toString())
            } catch (e: Exception) {
                logError("Error deserializing error response")
            }
        }
    }

    // helper method for showing errors and exceptions
    private fun logError(errorMsg: String) {
        _errorMsg.postValue(errorMsg)
        Log.e("**logged error in Main ViewModel", errorMsg)
    }
}