package com.example.weather

import com.example.weather.network.WeatherService
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val weatherModule = module {
    single { WeatherService }
    viewModel { MainViewModel(get()) }
}