package com.kampplus.hava.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.kampplus.hava.data.repository.CityRepository
import com.kampplus.hava.model.City
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CityViewModel : ViewModel() {

    // Şehir listemizi tutan depo sınıfını çağırıyoruz
    private val repository = CityRepository()

    // Arayüzün (CityScreen) dinleyeceği şehirler listesi State'i
    private val _cities = MutableStateFlow(repository.getCities())
    val cities: StateFlow<List<City>> = _cities.asStateFlow()

}
