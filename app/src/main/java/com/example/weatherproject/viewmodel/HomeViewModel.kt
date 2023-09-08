package com.example.weatherproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherproject.model.ResponseCurrentWeather
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.repository.HomeRepository

import com.example.weatherproject.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: HomeRepository) : ViewModel() {


    val currentWeatherData = MutableLiveData<MyResponse<ResponseCurrentWeather>>()
    fun loadCurrentWeatherById(lat: String , lon: String) = viewModelScope.launch(Dispatchers.IO) {
        repository.currentWeatherByLocation(lat, lon).collect {
            currentWeatherData.postValue(it)
        }

    }

    val nextWeatherListData = MutableLiveData<MyResponse<List<ResponseNextWeather.ItemList>>>()
    fun loadNextWeather(lat: String, lon : String ) = viewModelScope.launch(Dispatchers.IO) {
        repository.nextWeather(lat , lon).collect {
            nextWeatherListData.postValue(it)
        }

    }


}



