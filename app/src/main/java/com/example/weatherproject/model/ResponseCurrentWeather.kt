package com.example.weatherproject.model


import com.google.gson.annotations.SerializedName

data class ResponseCurrentWeather(
    @SerializedName("base")
    val base: String?, // stations
    @SerializedName("clouds")
    val clouds: Clouds?,
    @SerializedName("cod")
    val cod: Int?, // 200
    @SerializedName("coord")
    val coord: Coord?,
    @SerializedName("dt")
    val dt: Int?, // 1661870592
    @SerializedName("id")
    val id: Int?, // 3163858
    @SerializedName("main")
    val main: Main?,
    @SerializedName("name")
    val name: String?, // Zocca
    @SerializedName("rain")
    val rain: Rain?,
    @SerializedName("sys")
    val sys: Sys?,
    @SerializedName("timezone")
    val timezone: Int?, // 7200
    @SerializedName("visibility")
    val visibility: Int?, // 10000
    @SerializedName("weather")
    val weather: List<Weather?>?,
    @SerializedName("wind")
    val wind: Wind?
) {
    data class Clouds(
        @SerializedName("all")
        val all: Int? // 100
    )

    data class Coord(
        @SerializedName("lat")
        val lat: Double?, // 44.34
        @SerializedName("lon")
        val lon: Double? // 10.99
    )

    data class Main(
        @SerializedName("feels_like")
        val feelsLike: Double?, // 298.74
        @SerializedName("grnd_level")
        val grndLevel: Int?, // 933
        @SerializedName("humidity")
        val humidity: Int?, // 64
        @SerializedName("pressure")
        val pressure: Int?, // 1015
        @SerializedName("sea_level")
        val seaLevel: Int?, // 1015
        @SerializedName("temp")
        val temp: Double?, // 298.48
        @SerializedName("temp_max")
        val tempMax: Double?, // 300.05
        @SerializedName("temp_min")
        val tempMin: Double? // 297.56
    )

    data class Rain(
        @SerializedName("1h")
        val h: Double? // 3.16
    )

    data class Sys(
        @SerializedName("country")
        val country: String?, // IT
        @SerializedName("id")
        val id: Int?, // 2075663
        @SerializedName("sunrise")
        val sunrise: Int?, // 1661834187
        @SerializedName("sunset")
        val sunset: Int?, // 1661882248
        @SerializedName("type")
        val type: Int? // 2
    )

    data class Weather(
        @SerializedName("description")
        val description: String?, // moderate rain
        @SerializedName("icon")
        val icon: String?, // 10d
        @SerializedName("id")
        val id: Int?, // 501
        @SerializedName("main")
        val main: String? // Rain
    )

    data class Wind(
        @SerializedName("deg")
        val deg: Int?, // 349
        @SerializedName("gust")
        val gust: Double?, // 1.18
        @SerializedName("speed")
        val speed: Double? // 0.62
    )
}