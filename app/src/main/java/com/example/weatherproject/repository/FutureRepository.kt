package com.example.weatherproject.repository

import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.server.ApiServices
import com.example.weatherproject.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject

class FutureRepository @Inject constructor(private val api: ApiServices){

    suspend fun tomorrow(lat: String, lon: String): Flow<MyResponse<List<ResponseNextWeather.ItemList>>> {
        return flow {
            emit(MyResponse.loading())
            val response = api.getNextWeather(lat, lon)

            when (response.code()) {
                in 200..202 -> {
                    val responseData = response.body()
                    if (responseData != null) {
                        val filteredData = filterDataForTime(responseData)
                        emit(MyResponse.success(filteredData))
                    } else {
                        emit(MyResponse.error("Response data is null"))
                    }
                }
                else -> emit(MyResponse.error("Error fetching data"))
            }
        }.catch { emit(MyResponse.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }


    private fun filterDataForTime(response: ResponseNextWeather): List<ResponseNextWeather.ItemList> {
        val filteredList = mutableListOf<ResponseNextWeather.ItemList>()
        val unixTimestampTomorrow = getUnixTimestampTomorrow()

        response.list?.forEach { item ->
            if (filteredList.isNotEmpty()){
                return@forEach
            }
            val unixTimestampItem = item.dt?.toLong()
            if (unixTimestampItem != null && unixTimestampItem >= unixTimestampTomorrow + (12 * 60 * 60) && unixTimestampItem < unixTimestampTomorrow + (18 * 60 * 60)) {
                filteredList.add(item)
                return@forEach
            }
        }
        return filteredList
    }

    private fun getUnixTimestampTomorrow(): Long {
        val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val tomorrow = now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusDays(1)
        val zoneOffset = ZoneOffset.ofHoursMinutes(3, 30)
        return tomorrow.toInstant(zoneOffset).epochSecond
    }


    suspend fun nextDay(lat: String, lon: String): Flow<MyResponse<List<ResponseNextWeather.ItemList>>> {
        return flow {
            emit(MyResponse.loading())
            val response = api.getNextWeather(lat, lon)

            when (response.code()) {
                in 200..202 -> {
                    val responseData = response.body()
                    if (responseData != null) {
                        val filteredItems = findItemsInTimeRange(responseData)
                        if (filteredItems.isNotEmpty()) {
                            emit(MyResponse.success(filteredItems))
                        } else {
                            emit(MyResponse.error("No items found in the specified time range"))
                        }
                    } else {
                        emit(MyResponse.error("Response data is null"))
                    }
                }
                else -> emit(MyResponse.error("Error fetching data"))
            }
        }.catch { emit(MyResponse.error(it.message.toString())) }
            .flowOn(Dispatchers.IO)
    }



    private fun findItemsInTimeRange(response: ResponseNextWeather): List<ResponseNextWeather.ItemList> {
        val tomorrow = getUnixTimestampTomorrow()
        val currentTime = System.currentTimeMillis() / 1000
        val itemsInTimeRange = mutableListOf<ResponseNextWeather.ItemList>()
        var previousDate: Long? = null

        for (item in response.list.orEmpty()) {
            val unixTimestampItem = item.dt?.toLong()
            if (unixTimestampItem != null) {
                val itemDate = unixTimestampItem / (24 * 60 * 60)
                val itemHour = getHourFromUnixTimestamp(unixTimestampItem)
                val test = tomorrow / (24 * 60 * 60)
                if (itemDate != test ){

                    if ((unixTimestampItem >= tomorrow + (12 * 60 * 60)) && (itemHour in 12..18)) {
                        if (previousDate == null || itemDate != previousDate) {
                            itemsInTimeRange.add(item)
                            previousDate = itemDate
                        }
                    } else if (unixTimestampItem > currentTime && itemHour in 12..18) {
                        if (previousDate == null || itemDate != previousDate) {
                            itemsInTimeRange.add(item)
                            previousDate = itemDate
                        }
                    }
                }


            }
        }
        return itemsInTimeRange
    }

    private fun getHourFromUnixTimestamp(unixTimestamp: Long): Int {
        val date = Date(unixTimestamp * 1000L)
        val cal = Calendar.getInstance().apply {
            timeZone = TimeZone.getTimeZone("UTC")
            time = date
        }
        return cal.get(Calendar.HOUR_OF_DAY)
    }

}
