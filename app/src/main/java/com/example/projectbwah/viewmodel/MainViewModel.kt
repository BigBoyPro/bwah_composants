package com.example.projectbwah.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.DefaultActivity
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.Species
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {


    private val dao by lazy { PetsDB.getDB(application).PetsDao()}
    val allPets: Flow<List<Pet>> = dao.getAllPets()
    val allSpeciess: Flow<List<Species>> = dao.getAllSpecies()

    var showBottomSheet = mutableStateOf(false)



    fun insertPet(pet: Pet) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertPet(pet)
        }
    }

    // function to insert some example pets
    fun insertExamplePets() {
        val pets = listOf(
            Pet(
                name = "Buddy",
                speciesId = 2,
                age = 3,
                breed = "Golden Retriever",
                description = null,
                weight = 30.0,
                height = 60.0,
                birthDate = null,
                dateAdopted = null,
                color = "Golden",
                isMale = true,
                isSterilized = null,
                isVaccinated = null,
                image = null
            )
        )
        pets.forEach { insertPet(it) }
    }

    fun addSpecies(speciesName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertSpecies(Species(name = speciesName))
        }
    }


    fun getSpeciesActivities(speciesId: Int): Flow<List<DefaultActivity>> {
        return dao.getDefaultActivitiesBySpecies(speciesId)
            .flowOn(Dispatchers.IO) // Ensure database operations are on IO dispatcher
    }
}