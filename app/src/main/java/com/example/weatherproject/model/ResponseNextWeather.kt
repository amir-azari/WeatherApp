package com.example.weatherproject.model


import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.util.Calendar
import java.util.Date

data class ResponseNextWeather(
    @SerializedName("city")
    val city: City?,
    @SerializedName("cnt")
    val cnt: Int?, // 40
    @SerializedName("cod")
    val cod: String?, // 200
    @SerializedName("list")
    val list: List<ItemList>?,
    @SerializedName("message")
    val message: Int? // 0
) {
    data class City(
        @SerializedName("coord")
        val coord: Coord?,
        @SerializedName("country")
        val country: String?, // IT
        @SerializedName("id")
        val id: Int?, // 3163858
        @SerializedName("name")
        val name: String?, // Zocca
        @SerializedName("population")
        val population: Int?, // 4593
        @SerializedName("sunrise")
        val sunrise: Int?, // 1693975453
        @SerializedName("sunset")
        val sunset: Int?, // 1694022319
        @SerializedName("timezone")
        val timezone: Int? // 7200
    ) {
        data class Coord(
            @SerializedName("lat")
            val lat: Double?, // 44.34
            @SerializedName("lon")
            val lon: Double? // 10.99
        )
    }

    data class ItemList(
        @SerializedName("clouds")
        val clouds: Clouds?,
        @SerializedName("dt")
        val dt: Int?, // 1693990800
        @SerializedName("dt_txt")
        val dtTxt: String?, // 2023-09-06 09:00:00
        @SerializedName("main")
        val main: Main?,
        @SerializedName("pop")
        @JsonAdapter(DoubleToIntAdapter::class)
        val pop: Int?, // 0
        @SerializedName("sys")
        val sys: Sys?,
        @SerializedName("visibility")
        val visibility: Int?, // 10000
        @SerializedName("weather")
        val weather: List<Weather?>?,
        @SerializedName("wind")
        val wind: Wind?
    ) {
        data class Clouds(
            @SerializedName("all")
            val all: Int? // 4
        )

        data class Main(
            @SerializedName("feels_like")
            val feelsLike: Double?, // 287.76
            @SerializedName("grnd_level")
            val grndLevel: Int?, // 938
            @SerializedName("humidity")
            val humidity: Int?, // 62
            @SerializedName("pressure")
            val pressure: Int?, // 1022
            @SerializedName("sea_level")
            val seaLevel: Int?, // 1022
            @SerializedName("temp")
            val temp: Double?, // 288.55
            @SerializedName("temp_kf")
            val tempKf: Double?, // -4.77
            @SerializedName("temp_max")
            val tempMax: Double?, // 293.32
            @SerializedName("temp_min")
            val tempMin: Double? // 288.55
        )

        data class Sys(
            @SerializedName("pod")
            val pod: String? // d
        )

        data class Weather(
            @SerializedName("description")
            val description: String?, // clear sky
            @SerializedName("icon")
            val icon: String?, // 01d
            @SerializedName("id")
            val id: Int?, // 800
            @SerializedName("main")
            val main: String? // Clear
        )

        data class Wind(
            @SerializedName("deg")
            val deg: Int?, // 18
            @SerializedName("gust")
            val gust: Double?, // 4.53
            @SerializedName("speed")
            val speed: Double? // 2.98
        )

    }
}
