package com.example.projectbwah.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.ActivityBase
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.ScheduleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class SpeciesActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy { PetsDB.getDB(application).PetsDao() }
    private val _speciesActivities = MutableStateFlow<List<DefaultActivity>>(emptyList())
    val speciesActivities: StateFlow<List<DefaultActivity>> = _speciesActivities

    private val _editingActivity = MutableStateFlow<DefaultActivity?>(null)
    val editingActivity: StateFlow<DefaultActivity?> = _editingActivity.asStateFlow()

    // Add activity form variables
    var name = mutableStateOf("")
    var scheduleTime = mutableStateOf<LocalTime?>(null)
    var scheduleDayOfWeekOrMonth = mutableStateOf<Int?>(null)
    var scheduleDate = mutableStateOf<LocalDate?>(null)
    var isDefault = mutableStateOf(false)


    var selectedScheduleType = mutableStateOf(ScheduleType.ONCE)

    fun onScheduleTypeChange(newType: ScheduleType) {
        selectedScheduleType.value = newType
        scheduleTime.value = null
        scheduleDayOfWeekOrMonth.value = null
        scheduleDate.value = null
    }


    fun onIsDefaultChange(bool: Boolean) {
        isDefault.value = bool
    }

    fun onScheduleTimeChange(time: LocalTime?) {
        scheduleTime.value = time
    }

    fun onScheduleDayOfWeekChange(dayOfWeek: Int?) {
        scheduleDayOfWeekOrMonth.value = dayOfWeek
    }

    fun onScheduleDateChange(date: LocalDate?) {
        scheduleDate.value = date
    }

    fun getSpeciesActivities(speciesId: Int?) {
        viewModelScope.launch {
            if (speciesId == null) {
                dao.getDefaultActivities().collect { activities ->
                    _speciesActivities.value = activities
                }
            }else {
                dao.getDefaultActivitiesBySpecies(speciesId).collect { activities ->
                    _speciesActivities.value = activities
                }
            }
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

    fun updateActivity(activity: ActivityBase) {
        if (activity is DefaultActivity) {
            viewModelScope.launch {
                dao.updateDefaultActivity(activity)
                getSpeciesActivities(activity.speciesId) // Refresh activities
                _editingActivity.value = null // Clear editing state
            }
        }
    }
}
