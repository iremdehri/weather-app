package com.kampplus.hava.model

data class WeatherForecast (
    val dayName: String,
    val date: String= "",
    val temp: String,
    val condition: String,
    val iconRes: Int=0,
    val iconCode: String = "01d",
    val isToday: Boolean=false,
)

data class HourlyForecast(
    val time: String,
    val temp: String,
    val windSpeed: String,
    val windDegree: Int
)
