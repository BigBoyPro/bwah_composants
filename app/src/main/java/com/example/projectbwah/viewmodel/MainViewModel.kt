package com.example.projectbwah.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.data.Species
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao by lazy { PetsDB.getDB(application).PetsDao()}
    val allPets: Flow<List<Pet>> = dao.getAllPets()


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
                species = Species.DOG,
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
}