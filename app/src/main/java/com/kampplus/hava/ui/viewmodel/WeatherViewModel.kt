package com.kampplus.hava.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kampplus.hava.BuildConfig
import com.kampplus.hava.data.api.GrokApiService
import com.kampplus.hava.data.api.WeatherApiService
import com.kampplus.hava.data.repository.CityRepository
import com.kampplus.hava.model.GrokMessage
import com.kampplus.hava.model.GrokRequest
import com.kampplus.hava.model.HourlyForecast
import com.kampplus.hava.model.WeatherForecast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

sealed interface WeatherUiState {
    object Loading : WeatherUiState
    data class Success(val forecasts: List<WeatherForecast>, val hourlyForecasts: List<HourlyForecast>) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherDetailViewModel : ViewModel() {
    private val apiService = WeatherApiService.create()
    private val cityRepository = CityRepository()

    private val grokRetrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val grokApiService = grokRetrofit.create(GrokApiService::class.java)

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val _aiMessage = MutableStateFlow("Sana bir mesajım var ✨")
    val aiMessage: StateFlow<String> = _aiMessage

    fun fetchWeatherForCity(cityId: Int) {
        _uiState.value = WeatherUiState.Loading

        val city = cityRepository.getCities().find { it.id == cityId }

        if (city != null) {
            viewModelScope.launch {
                try {
                    val response = apiService.getFiveDayForecast(cityName = city.name)
                    val dailyData = response.list.filter { item -> item.dtTxt.contains("12:00:00") }

                    val mappedForecasts = dailyData.mapIndexed { index, item ->
                        val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.dtTxt)
                        val dayName = SimpleDateFormat("EEEE", Locale.forLanguageTag("tr")).format(dateObj!!)

                        val fullDate = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("tr")).format(dateObj)

                        WeatherForecast(
                            dayName = dayName,
                            date = fullDate,
                            temp = item.main.temp.toInt().toString(),
                            condition = item.weather.firstOrNull()?.description?.replaceFirstChar { char -> char.uppercase() } ?: "",
                            iconCode = item.weather.firstOrNull()?.icon ?: "01d",
                            isToday = index == 0
                        )
                    }

                    val mappedHourly = response.list.take(8).map { item ->
                        val dateObj = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(item.dtTxt)
                        val timeString = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateObj!!)

                        HourlyForecast(
                            time = timeString,
                            temp = item.main.temp.toInt().toString(),
                            windSpeed = item.wind.speed.toString(),
                            windDegree = item.wind.deg
                        )
                    }

                    _uiState.value = WeatherUiState.Success(mappedForecasts, mappedHourly)

                    generateAiInsight(mappedForecasts)

                } catch (_: IOException) {
                    _uiState.value = WeatherUiState.Error("İnternet bağlantınızı kontrol edip tekrar deneyin.")
                } catch (e: HttpException) {
                    _uiState.value = WeatherUiState.Error("Sunucu hatası (${e.code()}). Lütfen daha sonra tekrar deneyin.")
                } catch (e: Exception) {
                    e.printStackTrace()
                    _uiState.value = WeatherUiState.Error("Beklenmeyen bir hata oluştu.")
                }
            }
        }
    }

    private fun generateAiInsight(forecasts: List<WeatherForecast>) {
        viewModelScope.launch {
            try {
                val today = forecasts.firstOrNull()
                val prompt = "Bugün hava ${today?.condition} ve ${today?.temp} derece. Kullanıcıya bu hava durumuna göre esprili, kısa ve samimi bir tavsiye ver. Sadece 1-2 cümle olsun."

                val request = GrokRequest(
                    model = "openai/gpt-oss-20b",
                    messages = listOf(
                        GrokMessage(role = "system", content = "Sen eğlenceli ve yardımsever bir hava durumu asistanısın. Yanıtların her zaman Türkçe olsun."),
                        GrokMessage(role = "user", content = prompt)
                    )
                )

                val response = grokApiService.getChatCompletion(
                    authHeader = "Bearer ${BuildConfig.GROQ_API_KEY}",
                    request = request
                )

                val aiResponseText = response.choices.firstOrNull()?.message?.content
                    ?: "Asistan şu an kahve molasında ✨"

                _aiMessage.value = aiResponseText

            } catch (e: HttpException) {
                // API'den dönen gerçek JSON hata detayını yakalıyoruz
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("AiError", "HTTP Hatası Kodu: ${e.code()}")
                Log.e("AiError", "API'nin Cevabı: $errorBody")

                _aiMessage.value = "Bulutlar iletişimi kesti, tavsiye veremiyorum."
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("AiError", "Sistem Hatası: ${e.localizedMessage}", e)
                _aiMessage.value = "Bulutlar iletişimi kesti, tavsiye veremiyorum."
            }
        }
    }
}
