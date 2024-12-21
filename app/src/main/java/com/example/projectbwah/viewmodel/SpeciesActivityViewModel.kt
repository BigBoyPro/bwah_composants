package com.example.projectbwah.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.produceState

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.PetsDB
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class SpeciesActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val dao by lazy { PetsDB.getDB(application).PetsDao() }

    private val _speciesActivities = MutableStateFlow<List<DefaultActivity>>(emptyList())
    val speciesActivities: Flow<List<DefaultActivity>> = _speciesActivities.asStateFlow()

    private val _editingActivity = MutableStateFlow<DefaultActivity?>(null)
    val editingActivity: StateFlow<DefaultActivity?> = _editingActivity.asStateFlow()

    fun getSpeciesActivities(speciesId: Int) {
        viewModelScope.launch {
            Log.d("SpeciesActivityViewModel", "Fetching activities for speciesId: $speciesId")
            val activities = dao.getDefaultActivitiesBySpecies(speciesId)
            activities.collect { activityList -> // Collect values from the Flow
                _speciesActivities.value = activityList // Update the MutableStateFlow
                Log.d("SpeciesActivityViewModel", "Species activities state updated: ${_speciesActivities.value}")
            }
        }
    }



    fun addActivity(activity: DefaultActivity) {
        viewModelScope.launch {
            dao.insertDefaultActivity(activity)
            getSpeciesActivities(activity.speciesId) // Refresh activities
        }
    }

    fun deleteActivity(activity: DefaultActivity) {
        viewModelScope.launch {
            dao.deleteDefaultActivity(activity)
            getSpeciesActivities(activity.speciesId) // Refresh activities
        }
    }


    fun onEditActivity(activity: DefaultActivity?) {
        _editingActivity.value = activity
    }

    fun updateActivity(activity: DefaultActivity) {
        viewModelScope.launch {
            dao.updateDefaultActivity(activity)
            getSpeciesActivities(activity.speciesId) // Refresh activities
            _editingActivity.value = null // Clear editing state

        }
    }

}
