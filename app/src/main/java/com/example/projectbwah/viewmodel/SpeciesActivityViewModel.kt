package com.example.projectbwah.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.ScheduleType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class SpeciesActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy { PetsDB.getDB(application).PetsDao() }

    private val _speciesActivities = MutableStateFlow<List<DefaultActivity>>(emptyList())
    val speciesActivities: StateFlow<List<DefaultActivity>> = _speciesActivities.asStateFlow()

    private val _editingActivity = MutableStateFlow<DefaultActivity?>(null)
    val editingActivity: StateFlow<DefaultActivity?> = _editingActivity.asStateFlow()

    // Add activity form variables
    var activityName = mutableStateOf("")
    var scheduleTime = mutableStateOf<LocalTime?>(null)
    var scheduleDayOfWeek = mutableStateOf<Int?>(null)
    var scheduleDate = mutableStateOf<LocalDate?>(null)
    var isDefault = mutableStateOf(false)


    var selectedScheduleType =  mutableStateOf<ScheduleType?>(null)

    fun onScheduleTypeChange(newType: ScheduleType?) {
        selectedScheduleType.value = newType
        scheduleTime.value = null
        scheduleDayOfWeek.value = null
        scheduleDate.value = null
    }

    fun onActivityNameChange(name: String) {
        activityName.value = name
    }

    fun onIsDefaultChange(bool: Boolean) {
        isDefault.value = bool
    }

    fun onScheduleTimeChange(time: LocalTime?) {
        scheduleTime.value = time
    }

    fun onScheduleDayOfWeekChange(dayOfWeek: Int?) {
        scheduleDayOfWeek.value = dayOfWeek
    }

    fun onScheduleDateChange(date: LocalDate?) {
        scheduleDate.value = date
    }

    fun getSpeciesActivities(speciesId: Int) {
        viewModelScope.launch {
            Log.d("SpeciesActivityViewModel", "Fetching activities for speciesId: $speciesId")
            dao.getDefaultActivitiesBySpecies(speciesId).collect { activityList ->
                _speciesActivities.value = activityList
                Log.d("SpeciesActivityViewModel", "Species activities state updated: ${_speciesActivities.value}")
            }
        }
    }

    fun addActivity(activity: DefaultActivity) {
        val isValid = when (selectedScheduleType.value) {
            ScheduleType.DAILY -> activityName.value.isNotBlank() && scheduleTime.value != null
            ScheduleType.WEEKLY -> activityName.value.isNotBlank() && scheduleDayOfWeek.value != null && scheduleTime.value != null
            ScheduleType.ONCE -> activityName.value.isNotBlank() && scheduleDate.value != null && scheduleTime.value != null
            else -> false
        }
        if (!isValid) {
            Log.e("", "Add button is disabled")
            return
        }
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
