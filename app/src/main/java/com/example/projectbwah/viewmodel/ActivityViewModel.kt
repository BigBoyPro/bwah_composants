package com.example.projectbwah.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.ActivityBase
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.PetActivity
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.ScheduleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy { PetsDB.getDB(application).PetsDao() }


    var isPetActivity  = mutableStateOf(true)
    var activityId = mutableStateOf<Int?>(null)
    var petId = mutableStateOf<Int?>(null)
    var speciesId = mutableStateOf<Int?>(null)
    val scheduleTypes = ScheduleType.entries.toList()


    var activity = mutableStateOf<ActivityBase?>(null)
    var finished = mutableStateOf(false)

    val name = mutableStateOf("")
    var nameError = mutableStateOf("")

    val scheduleType = mutableStateOf(ScheduleType.ONCE)
    var scheduleTypeError = mutableStateOf("")

    val scheduleTime = mutableStateOf<LocalTime?>(null)
    val scheduleDayOfWeekOrMonth = mutableStateOf<Int?>(null)

    val scheduleDate = mutableStateOf<LocalDate?>(null)
    var scheduleDateError = mutableStateOf("")


    val isDefault = mutableStateOf(false)


    fun loadActivity(activityId: Int?) {
        if (activityId == null || (isPetActivity.value && petId.value == null)) {
            return
        }
        this.activityId.value = activityId

        viewModelScope.launch {
            if (isPetActivity.value) {
                dao.getPetActivityById(activityId).collectLatest { loadedActivity ->
                    activity.value = loadedActivity
                    clearStates(loadedActivity)
                }
            } else {
                dao.getDefaultActivityById(activityId).collectLatest { loadedActivity ->
                    activity.value = loadedActivity

                    clearStates(loadedActivity)
                }
            }
        }
    }

    fun clearErrorStates() {
        nameError.value = ""
        scheduleTypeError.value = ""
        scheduleDateError.value = ""
    }

    fun clearStates(loadedActivity: ActivityBase? = activity.value) {
        // clear all states
        clearErrorStates()
        if (activityId.value == null) activity.value = null
        name.value = loadedActivity?.name ?: ""
        scheduleType.value = loadedActivity?.scheduleType ?: ScheduleType.ONCE
        scheduleTime.value = loadedActivity?.scheduleTime
        scheduleDayOfWeekOrMonth.value = loadedActivity?.scheduleDayOfWeekOrMonth
        scheduleDate.value = (loadedActivity as? PetActivity)?.scheduleDate
        isDefault.value = (loadedActivity as? DefaultActivity)?.isDefault ?: false

    }

    fun hasChanges(): Boolean {
        val loadedActivity = activity.value
        if (loadedActivity == null) {
            return name.value.isNotBlank() ||
                    scheduleTime.value != null ||
                    scheduleDayOfWeekOrMonth.value != null ||
                    scheduleDate.value != null
        } else {
            return name.value != loadedActivity.name ||
                    scheduleType.value != loadedActivity.scheduleType ||
                    scheduleTime.value != loadedActivity.scheduleTime ||
                    scheduleDayOfWeekOrMonth.value != loadedActivity.scheduleDayOfWeekOrMonth ||
                    (loadedActivity is PetActivity && scheduleDate.value != loadedActivity.scheduleDate) ||
                    (loadedActivity is DefaultActivity && isDefault.value != loadedActivity.isDefault)
        }
    }


    private fun trimErrorStates() {
        nameError.value = nameError.value.trim()
        scheduleTypeError.value = scheduleTypeError.value.trim()
        scheduleDateError.value = scheduleDateError.value.trim()
    }

    private fun clearAndCheckErrors(): Boolean {

        clearErrorStates()
        var hasErrors = false

        if (name.value.isBlank()) {
            nameError.value += "Name is required\n"
            hasErrors = true
        }

        when (scheduleType.value) {
            ScheduleType.ONCE -> {
                val scheduleDate = scheduleDate.value
                if (isPetActivity.value ) {
                    if(scheduleDate == null) {
                        scheduleDateError.value += "Date is required for once schedule\n"
                        hasErrors = true
                    }else if(scheduleDate < LocalDate.now()){
                        scheduleDateError.value += "Date cannot be in the past\n"
                        hasErrors = true
                    }
                }
                val scheduleTime = scheduleTime.value
                if (scheduleTime == null) {
                    scheduleDateError.value += "Time is required for once schedule\n"
                    hasErrors = true
                }else if(isPetActivity.value  && scheduleTime < LocalTime.now() && scheduleDate == LocalDate.now()){
                    scheduleDateError.value += "Time cannot be in the past\n"
                    hasErrors = true
                }
            }
            ScheduleType.DAILY -> {
                if (scheduleTime.value == null) {
                    scheduleTypeError.value += "Time is required for daily schedule\n"
                    hasErrors = true
                }
            }

            ScheduleType.WEEKLY -> {
                if (scheduleDayOfWeekOrMonth.value == null) {
                    scheduleTypeError.value += "Day of week is required for weekly schedule\n"
                    hasErrors = true
                }
                if (scheduleTime.value == null) {
                    scheduleTypeError.value += "Time is required for weekly schedule\n"
                    hasErrors = true
                }
            }

            ScheduleType.MONTHLY -> {
                if (scheduleDayOfWeekOrMonth.value == null) {
                    scheduleTypeError.value = "Day of month is required for monthly schedule\n"
                    hasErrors = true
                }
                if (scheduleTime.value == null) {
                    scheduleTypeError.value = "Time is required for monthly schedule\n"
                    hasErrors = true
                }
            }
        }
        trimErrorStates()

        return hasErrors
    }


    fun addOrUpdateActivity() {
        if (clearAndCheckErrors()) {
            return
        }
        val activityId = this.activityId.value
        val petId = this.petId.value
        val speciesId = this.speciesId.value

        val newActivity = if (isPetActivity.value  && petId != null) {
            if (activityId != null) {
                PetActivity(
                    id = activityId,
                    name = name.value,
                    scheduleType = scheduleType.value,
                    scheduleTime = scheduleTime.value,
                    scheduleDayOfWeekOrMonth = scheduleDayOfWeekOrMonth.value,
                    scheduleDate = scheduleDate.value,
                    petId = petId
                )
            } else {
                PetActivity(
                    name = name.value,
                    scheduleType = scheduleType.value,
                    scheduleTime = scheduleTime.value,
                    scheduleDayOfWeekOrMonth = scheduleDayOfWeekOrMonth.value,
                    scheduleDate = scheduleDate.value,
                    petId = petId
                )

            }
        }else {
            if (activityId != null) {
                DefaultActivity(
                    id = activityId,
                    name = name.value,
                    speciesId = speciesId,
                    isDefault = isDefault.value,
                    scheduleType = scheduleType.value,
                    scheduleTime = scheduleTime.value,
                    scheduleDayOfWeekOrMonth = scheduleDayOfWeekOrMonth.value
                )
            } else {
                DefaultActivity(
                    name = name.value,
                    speciesId = speciesId,
                    isDefault = isDefault.value,
                    scheduleType = scheduleType.value,
                    scheduleTime = scheduleTime.value,
                    scheduleDayOfWeekOrMonth = scheduleDayOfWeekOrMonth.value
                )
            }
        }


        viewModelScope.launch {
            val success: Boolean
            if (isPetActivity.value) {
                val petActivity = newActivity as PetActivity
                if (activityId == null) {
                    success = dao.insertPetActivity(petActivity) != -1L
                } else {
                    success = dao.updatePetActivity(petActivity) > 0
                    if (success) activity.value = newActivity
                }
            } else {
                val defaultActivity = newActivity as DefaultActivity
                if (activityId == null) {
                    success = dao.insertDefaultActivity(defaultActivity) != -1L
                } else {
                    val s = dao.updateDefaultActivity(defaultActivity)
                    success = s > 0
                    if (success) activity.value = newActivity
                }
            }
            finished.value = success
        }
    }

    fun initialize(petActivity: Boolean, petId: Int?, speciesId: Int?) {
        this.isPetActivity.value = petActivity
        this.petId.value = petId
        this.speciesId.value = speciesId
    }

    fun deleteActivity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isPetActivity.value) {
                    dao.deletePetActivityById(activityId.value!!)
                } else {
                    dao.deleteDefaultActivityById(activityId.value!!)
                }
            }
        }
    }


}