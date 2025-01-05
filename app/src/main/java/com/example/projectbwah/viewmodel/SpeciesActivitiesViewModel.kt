package com.example.projectbwah.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SpeciesActivitiesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy { PetsDB.getDB(application).PetsDao() }
    private val _speciesActivities = MutableStateFlow<List<DefaultActivity>>(emptyList())
    val speciesActivities: StateFlow<List<DefaultActivity>> = _speciesActivities




    private val _editingActivity = MutableStateFlow<DefaultActivity?>(null)
    val editingActivity: StateFlow<DefaultActivity?> = _editingActivity.asStateFlow()


    fun getSpeciesActivities(speciesId: Int?) {
        viewModelScope.launch {
            if (speciesId == null) {
                dao.getDefaultDefaultActivities().collect { activities ->
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
            val speciesId = activity.speciesId
            dao.deleteDefaultActivity(activity)
            getSpeciesActivities(speciesId) // Refresh activities
        }
    }

    fun onEditActivity(activity: DefaultActivity?) {
        _editingActivity.value = activity
    }




//    fun getPetsBySpecies(speciesId: Int): Flow<List<Pet>> {
//        return dao.getPetsBySpecies(speciesId).flowOn(Dispatchers.IO)
//    }



}
