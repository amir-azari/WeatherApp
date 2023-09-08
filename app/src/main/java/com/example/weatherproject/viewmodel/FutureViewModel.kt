package com.example.weatherproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.model.ResponseCurrentWeather
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.repository.FutureRepository
import com.example.weatherproject.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FutureViewModel @Inject constructor(private val repository: FutureRepository) : ViewModel(){

    val tomorrowWeatherData = MutableLiveData<MyResponse<List<ResponseNextWeather.ItemList>>>()
    fun loadTomorrowWeather(lat : String , lon: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.tomorrow(lat , lon).collect {
            tomorrowWeatherData.postValue(it)
        }

    }
    val netDayWeatherData = MutableLiveData<MyResponse<List<ResponseNextWeather.ItemList>>>()

    fun loadNextDayWeather(lat : String , lon: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.nextDay(lat , lon).collect {
            netDayWeatherData.postValue(it)
        }

    }

}