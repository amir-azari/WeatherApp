package com.example.weatherproject.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreLocationData @Inject constructor(@ApplicationContext val context: Context) {

    companion object{
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(Constants.DataStor.LOCATION_INFO_DATASTORE)
        val lat = doublePreferencesKey(Constants.DataStor.LAT_LOCATION)
        val lon = doublePreferencesKey(Constants.DataStor.LON_LOCATION)

    }
    suspend fun saveLocation(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[lat] = latitude
            preferences[lon] = longitude
        }
    }

    fun getLocation(): Flow<Pair<Double?, Double?>> {
        return context.dataStore.data.map { preferences ->
            Pair(preferences[lat], preferences[lon])
        }
    }
}