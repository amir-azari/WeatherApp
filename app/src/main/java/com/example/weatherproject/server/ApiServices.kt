package com.example.weatherproject.server

import com.example.weatherproject.model.ResponseCurrentWeather
import com.example.weatherproject.model.ResponseNextWeather
import com.example.weatherproject.utils.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServices {

    @GET("weather")
    suspend fun getCurrentWeatherByName(
        @Query("q") name: String,
        @Query("appid") appid: String = Constants.NetworkService.API_KEY_VALUE ,
        @Query("units") units: String = "metric"
    ) : Response<ResponseCurrentWeather>

    @GET("weather")
    suspend fun getCurrentWeatherByGeocoder(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = Constants.NetworkService.API_KEY_VALUE ,
        @Query("units") units: String = "metric"
    ) : Response<ResponseCurrentWeather>

    @GET("forecast")
    suspend fun getNextWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = Constants.NetworkService.API_KEY_VALUE ,
        @Query("units") units: String = "metric"
    ) : Response<ResponseNextWeather>
}