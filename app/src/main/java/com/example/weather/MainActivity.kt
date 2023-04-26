package com.example.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.models.WeatherResponse
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso

// TODO add dependecny injection using koin and inject service and viewmodel
class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val ACCESS_COARSE_LOCATION = 100

    // TODO save last city searched and use to prepopulate on app launch, if no search use user current location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationWeather()

        with(viewModel) {
            errorMsg.observe(this@MainActivity, errorObserver)
            weatherDetails.observe(this@MainActivity, weatherObserver)
        }
    }

    // sends user entered city to service to service to retrieve weather
    // TODO handle empty city name
    override fun onQueryTextSubmit(query: String?): Boolean {
        query?.let { viewModel.getCityData(it) }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    private fun initUI() {
        binding.weatherDetailContainer.visibility = View.GONE
        binding.searchView.setOnQueryTextListener(this)
    }

    private fun checkLocationWeather() {
        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION, ACCESS_COARSE_LOCATION)
    }

    private fun checkSelfPermission(permission: String, requestCode: Int) {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // requests access for permission
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(permission),
                requestCode
            )
        } else {
            // uses location to get weather
            fusedLocationClient.lastLocation.addOnSuccessListener {
                val lat = it.latitude
                val long = it.longitude
                getLastKnownLocation(lat, long)
            }
        }
    }

    // handles location permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            ACCESS_COARSE_LOCATION -> {
                when {
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        showToast(
                            "Location granted"
                        )
                        checkLocationWeather()
                    }
                    else -> {
                        showToast("Location not granted")
                    }
                }
            }
        }
    }

    // uses users location to get weather
    private fun getLastKnownLocation(lat: Double, long: Double) {
        viewModel.getCityDataByCoordinates(lat, long)
        showToast(getString(R.string.current_location_prompt))
    }

    private val weatherObserver = Observer<WeatherResponse> {
        // Todo add country as some cities have the same name
        binding.weatherDetailContainer.visibility = View.VISIBLE
        with(binding) {
            it.weather[0].icon?.let { iconId ->
                loadImage(iconId)
            }
            it.name?.let { cityName -> name.text = cityName }
            it.main?.temp?.let { temp ->
                temperature.text = temp.toString()
            }
            it.main?.temp_min?.let { min -> tempMin.text = min.toString() }
            it.main?.temp_max?.let { max -> tempMax.text = max.toString() }
            it.main?.humidity?.let { hum -> humidity.text = hum.toString() }
        }
    }

    private fun loadImage(iconId: String) {
        val imgURL = "https://openweathermap.org/img/wn/${iconId}@2x.png"
        Picasso.get().load(imgURL).fit().into(binding.icon)
    }

    private val errorObserver = Observer<String> {
        showToast(it)
    }

    // helper to show toast messages
    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
    }
}