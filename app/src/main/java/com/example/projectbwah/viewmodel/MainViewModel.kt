package com.example.projectbwah.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.Species
import com.example.projectbwah.utils.ThemeHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val dao by lazy { PetsDB.getDB(application).PetsDao()}
    val allPets: Flow<List<Pet>> = dao.getAllPets()
    val allSpecies: Flow<List<Species>> = dao.getAllSpecies()

    var showBottomSheet = mutableStateOf(false)

    val selectedPets = mutableStateListOf<Pet>()
    var showDeleteConfirmationDialog =mutableStateOf(false)

    fun addSpecies(speciesName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertSpecies(Species(name = speciesName))
        }
    }


    fun getSpeciesActivities(speciesId: Int): Flow<List<DefaultActivity>> {
        return dao.getDefaultActivitiesBySpecies(speciesId)
            .flowOn(Dispatchers.IO) // Ensure database operations are on IO dispatcher
    }


    fun deleteSelectedPets() {
        viewModelScope.launch {
            selectedPets.forEach { pet ->
                dao.deletePet(pet)
            }
            selectedPets.clear() // Clear the selected pets list in the ViewModel
        }
    }


    /*
   theme helper
    */

    private val _isDarkTheme = MutableStateFlow(ThemeHelper(getApplication()).isDarkTheme()) // Initialize with Shared Preferences value
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkTheme.value = !_isDarkTheme.value
            ThemeHelper(getApplication()).setDarkTheme(_isDarkTheme.value) // Update Shared Preferences
        }
    }

}