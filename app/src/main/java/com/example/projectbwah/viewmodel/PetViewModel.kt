package com.example.projectbwah.viewmodel



import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao by lazy { PetsDB.getDB(application).PetsDao() }

    var name = mutableStateOf("")
    var speciesId = mutableStateOf(1)
    var age = mutableStateOf("")
    var breed = mutableStateOf("")
    var description = mutableStateOf("")
    var weight = mutableStateOf("")
    var height = mutableStateOf("")
    var birthDate = mutableStateOf(LocalDate.now())
    var dateAdopted = mutableStateOf(LocalDate.now())
    var color = mutableStateOf("")
    var isMale = mutableStateOf(true)
    var isSterilized = mutableStateOf(false)
    var isVaccinated = mutableStateOf(false)
//    var image = mutableStateOf("")
    var imageUri = mutableStateOf<Uri?>(null)




    var showDatePicker = mutableStateOf(false)
    var showAdoptedDatePicker = mutableStateOf(false)

    var showErrors = mutableStateOf(false)




    var selectedSpeciesName = mutableStateOf("")
    val speciesList = dao.getAllSpecies().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )



    fun addPet() {

        val speciesId = speciesList.value.find { it.name == selectedSpeciesName.value }?.id ?: -1
        Log.d("AddPetViewModel", "name: ${name.value} , speciesId: $speciesId, age: ${age.value}, breed: ${breed.value}, color: ${color.value}, weight: ${weight.value}, height: ${height.value}")

        if (speciesId == -1 || name.value.isBlank() || age.value.isBlank() || breed.value.isBlank() || color.value.isBlank() || weight.value.isBlank() || height.value.isBlank()) {

            showErrors.value = true

        } else {
            viewModelScope.launch {
                val newPet = Pet(
                    name = name.value,
                    speciesId = speciesId,
                    age = age.value.toIntOrNull(),
                    breed = breed.value,
                    description = description.value,
                    weight = weight.value.toDoubleOrNull(),
                    height = height.value.toDoubleOrNull(),
                    birthDate = birthDate.value,
                    dateAdopted = dateAdopted.value,
                    color = color.value,
                    isMale = isMale.value,
                    isSterilized = isSterilized.value,
                    isVaccinated = isVaccinated.value,
                    image = imageUri.value.toString()
                )
                dao.insertPet(newPet)
                showErrors.value = false

            }
        }

    }


    /*
    EDIT
     */

    var pet by mutableStateOf<Pet?>(null) // State to hold the pet data
    fun loadPet(petId: Int) {
        viewModelScope.launch {
            dao.getPetById(petId).collectLatest { loadedPet -> // Collect latest value from Flow
                pet = loadedPet // Update pet state with the collected value
                // Update other state variables with pet data
                // ...
            }
        }
    }

    fun updatePet(petId: Int) {
        // Validate data (similar to AddPetViewModel)
        // ...

        val speciesId = speciesList.value.find { it.name == selectedSpeciesName.value }?.id ?: -1
        Log.d("AddPetViewModel", "imageUri: ${imageUri.value} , speciesId: $speciesId, age: ${age.value}, breed: ${breed.value}, color: ${color.value}, weight: ${weight.value}, height: ${height.value}")

        if (speciesId == -1 || name.value.isBlank() || age.value.isBlank() || breed.value.isBlank() || color.value.isBlank() || weight.value.isBlank() || height.value.isBlank()) {

            showErrors.value = true

        } else {
            viewModelScope.launch {
                val newPet = Pet(
                    idPet = petId,
                    name = name.value,
                    speciesId = speciesId,
                    age = age.value.toIntOrNull(),
                    breed = breed.value,
                    description = description.value,
                    weight = weight.value.toDoubleOrNull(),
                    height = height.value.toDoubleOrNull(),
                    birthDate = birthDate.value,
                    dateAdopted = dateAdopted.value,
                    color = color.value,
                    isMale = isMale.value,
                    isSterilized = isSterilized.value,
                    isVaccinated = isVaccinated.value,
                    image = imageUri.value.toString()
                )
                dao.updatePet(newPet)
                showErrors.value = false

            }
        }
    }

    fun getSpeciesNameById(speciesId: Int): String {
        val species = speciesList.value.find { it.id == speciesId }
        return species?.name ?: ""
    }



    /*
    Delete activity
     */

    var showDeleteConfirmationDialog = mutableStateOf(false)

    fun deletePet(petId: Int) {
        viewModelScope.launch {
            dao.getPetById(petId).collectLatest { loadedPet ->
                dao.deletePet(loadedPet) // Delete the pet directly inside collectLatest
            }
        }
    }


}