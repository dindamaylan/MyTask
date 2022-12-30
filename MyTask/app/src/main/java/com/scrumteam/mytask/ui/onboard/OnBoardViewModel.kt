package com.scrumteam.mytask.ui.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.scrumteam.mytask.data.preferences.SettingsPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardViewModel @Inject constructor(private val settingsPreferences: SettingsPreferences) :
    ViewModel() {

    val isFirstRunOnBoard: LiveData<Boolean> = settingsPreferences.isFirstRunOnBoard.asLiveData()

    fun updateIsFirstRunOnBoard(value: Boolean){
        viewModelScope.launch {
            settingsPreferences.updateIsFirstRunOnBoard(value)
        }
    }
}