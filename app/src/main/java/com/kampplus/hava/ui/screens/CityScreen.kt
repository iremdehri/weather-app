package com.kampplus.hava.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kampplus.hava.ui.viewmodel.CityViewModel
import com.kampplus.hava.theme.LightBlue
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityScreen(
    viewModel: CityViewModel = viewModel(),
    onCityClick: (Int) -> Unit
) {
    val cities by viewModel.cities.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCities = cities.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    // Modern arka plan gradyanı
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFE4F0FE), Color(0xFFF7F8FA))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "Hava Durumu",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(bottom = 16.dp, start = 8.dp)
            )

            // Şık Arama Çubuğu
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("İl ara...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(32.dp),
                colors = OutlinedTextFieldDefaults.colors( // <-- Güncellenen kısım
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF8EB6FA)
                )
            )

            // Modern Şehir Kartları
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredCities) { city ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onCityClick(city.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color.White, LightBlue)
                                    )
                                )
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = city.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Detaya Git",
                                tint = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}
