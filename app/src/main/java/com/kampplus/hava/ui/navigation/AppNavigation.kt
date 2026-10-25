package com.kampplus.hava.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kampplus.hava.ui.screens.CityScreen
import com.kampplus.hava.ui.screens.WeatherDetailScreen
import com.kampplus.hava.ui.viewmodel.WeatherDetailViewModel
import com.kampplus.hava.ui.viewmodel.WeatherUiState

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "city_list") {

        composable("city_list") {
            CityScreen(onCityClick = { cityId ->
                navController.navigate("weather_detail/$cityId")
            })
        }

        composable(
            route = "weather_detail/{cityId}",
            arguments = listOf(navArgument("cityId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cityId = backStackEntry.arguments?.getInt("cityId") ?: 1
            val detailViewModel: WeatherDetailViewModel = viewModel()

            LaunchedEffect(cityId) {
                detailViewModel.fetchWeatherForCity(cityId)
            }

            // Artık listeyi değil, UiState'i dinliyoruz
            val uiState by detailViewModel.uiState.collectAsState()

            when (uiState) {
                is WeatherUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is WeatherUiState.Success -> {
                    val successState = uiState as WeatherUiState.Success
                    val forecasts = successState.forecasts
                    val hourlyForecasts = successState.hourlyForecasts // Saatlik veriyi state'ten çektik

                    val aiMessage by detailViewModel.aiMessage.collectAsState()
                    WeatherDetailScreen(
                        forecasts = forecasts,
                        hourlyForecasts = hourlyForecasts,
                        aiMessage = aiMessage
                    )
                }
                is WeatherUiState.Error -> {
                    val errorMessage = (uiState as WeatherUiState.Error).message
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = { detailViewModel.fetchWeatherForCity(cityId) } // Butona basınca tekrar istek atar
                    )
                }
            }
        }
    }
}

// Hata Ekranı ve Tekrar Dene Butonu
@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Hata",
            modifier = Modifier.size(64.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry) {
            Text("Tekrar Dene")
        }
    }
}
