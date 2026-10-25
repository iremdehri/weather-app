package com.kampplus.hava.data.api

import com.google.gson.annotations.SerializedName

// API'den gelen ana yanıt
data class ForecastResponse(
    val list: List<ForecastItem>,
    val city: ApiCity
)

data class ApiCity(
    val name: String
)

data class ForecastItem(
    @SerializedName("dt_txt") val dtTxt: String, // Tarih ve saat: "2030-01-06 12:00:00"
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: Wind
)

data class MainData(
    val temp: Double,
    val description: String, // "parçalı bulutlu" vb.
    val icon: String
)

data class WeatherData(
    val description: String, // "parçalı bulutlu" vb.
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)
