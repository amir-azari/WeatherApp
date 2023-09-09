package com.example.weatherproject.utils

object Constants {
    object NetworkService {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        const val API_KEY_VALUE = "48729f3d019549f63a766207a997db6e"
        const val CONNECTION_TIME = 30L

    }
    object DataStor{

        const val LOCATION_INFO_DATASTORE = "location_info_datastore"
        const val LAT_LOCATION = "lat_location"
        const val LON_LOCATION = "lon_location"
    }

}