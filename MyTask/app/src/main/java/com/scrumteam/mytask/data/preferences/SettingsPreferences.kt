package com.scrumteam.mytask.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.scrumteam.mytask.utils.Constants.FIRST_RUN_ONBOARD
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsPreferences @Inject constructor(private val settingsPreferences: DataStore<Preferences>) {
    val isFirstRunOnBoard: Flow<Boolean> = settingsPreferences.data.map { preferences ->
        preferences[PreferencesKeys.FIRST_RUN_ONBOARD_KEY] ?: true
    }

    suspend fun updateIsFirstRunOnBoard(value: Boolean){
        settingsPreferences.edit { preferences ->
            preferences[PreferencesKeys.FIRST_RUN_ONBOARD_KEY] = value
        }
    }

    private object PreferencesKeys {
        val FIRST_RUN_ONBOARD_KEY = booleanPreferencesKey(FIRST_RUN_ONBOARD)
    }
}