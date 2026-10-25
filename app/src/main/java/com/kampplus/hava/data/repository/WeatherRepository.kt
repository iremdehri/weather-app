package com.kampplus.hava.data.repository

import com.kampplus.hava.model.City
import com.kampplus.hava.model.WeatherForecast

interface WeatherRepository {
    suspend fun getCities(): List<City>
    suspend fun getFiveDayForecast(cityId: Int): List<WeatherForecast>
}
