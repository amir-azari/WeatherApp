package com.example.weatherproject.repository

import android.icu.util.Calendar
import com.example.weatherproject.model.ResponseCurrentWeather
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.server.ApiServices
import com.example.weatherproject.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class HomeRepository @Inject constructor(private val api: ApiServices) {

    suspend fun currentWeatherByLocation(lat: String, lon : String ): Flow<MyResponse<ResponseCurrentWeather>> {
        return flow {
            emit(MyResponse.loading())
            when (api.getCurrentWeatherByGeocoder(lat , lon).code()) {
                in 200..202 -> {
                    emit(MyResponse.success(api.getCurrentWeatherByGeocoder(lat, lon).body())
                    )
                }
            }
        }.catch { emit(MyResponse.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }
    suspend fun nextWeather(lat: String , lon : String ): Flow<MyResponse<List<ResponseNextWeather.ItemList>>> {
        return flow {
            emit(MyResponse.loading())
            when ( api.getNextWeather(lat , lon).code()) {
                in 200..202 -> {
                    val response = api.getNextWeather(lat, lon).body()
                    if (response != null) {
                        val currentDate = getCurrentDateInTimeZone(response.city?.timezone ?: 0)
                        val filteredList = response.list?.filter { it.dtTxt?.startsWith(currentDate) == true }
                        emit(MyResponse.success(filteredList))
                    } else {
                        emit(MyResponse.error("Response body is null"))
                    }
                }
            }
        }.catch { emit(MyResponse.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }

    private fun getCurrentDateInTimeZone(timezoneOffset: Int): String {
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.SECOND, timezoneOffset)
        val dateFormat = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

}