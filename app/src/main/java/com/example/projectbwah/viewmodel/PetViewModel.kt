package com.example.projectbwah.viewmodel


import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectbwah.data.Pet
import com.example.projectbwah.data.PetsDB
import com.example.projectbwah.utils.saveImageToInternalStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class PetViewModel(application: Application) : AndroidViewModel(application) {
    private val dao by lazy { PetsDB.getDB(application).PetsDao() }

    private var petId: Int? = null
    var pet = mutableStateOf<Pet?>(null)
    var finish = mutableStateOf(false)
    var name = mutableStateOf("")
    var nameError = mutableStateOf("")
    var breed = mutableStateOf("")
    var breedError = mutableStateOf("")
    var description = mutableStateOf("")
    var descriptionError = mutableStateOf("")
    var weight = mutableStateOf("")
    var weightError = mutableStateOf("")
    var height = mutableStateOf("")
    var heightError = mutableStateOf("")
    var birthDate = mutableStateOf<LocalDate?>(null)
    var birthDateError = mutableStateOf("")
    var adoptedDate = mutableStateOf<LocalDate?>(null)
    var adoptedDateError = mutableStateOf("")
    var color = mutableStateOf("")
    var colorError = mutableStateOf("")
    var isMale = mutableStateOf(true)
    var isSterilized = mutableStateOf(false)
    var imageUri = mutableStateOf<Uri?>(null)


    var selectedSpeciesName = mutableStateOf("")
    val speciesList = dao.getAllSpecies().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun loadPet(petId: Int?) {
        if(petId == null || petId == this.petId) {
            return
        }
        this.petId = petId

        viewModelScope.launch {
            dao.getPetById(petId).collectLatest { loadedPet ->
                pet.value = loadedPet
                clearStates(loadedPet)

            }
        }
    }

    fun clearStates(loadedPet: Pet? = pet.value) {
        // clear all states
        clearErrorStates()
        finish.value = false
        if(petId == null) pet.value = null
        selectedSpeciesName.value = getSpeciesNameById(loadedPet?.speciesId)
        name.value = loadedPet?.name ?: ""
        breed.value = loadedPet?.breed ?: ""
        description.value = loadedPet?.description ?: ""
        weight.value = loadedPet?.weight?.toString() ?: ""
        height.value = loadedPet?.height?.toString() ?: ""
        birthDate.value = loadedPet?.birthDate
        adoptedDate.value = loadedPet?.adoptedDate
        color.value = loadedPet?.color ?: ""
        isMale.value = loadedPet?.isMale ?: true
        isSterilized.value = loadedPet?.isSterilized ?: false
        imageUri.value = if (loadedPet?.image != null) Uri.parse(loadedPet.image) else null
    }

    fun addOrUpdatePet(context: Context = getApplication<Application>().applicationContext) {
        if(clearAndCheckErrors()) {
            return
        }



        val speciesId = speciesList.value.find { it.name == selectedSpeciesName.value }?.id
        val imagePath = imageUri.value?.let { saveImageToInternalStorage(context, it) }
        val constantPetId = petId
        val newPet = if (constantPetId != null) {
            Pet(
                idPet = constantPetId,
                name = name.value,
                speciesId = speciesId,
                breed = breed.value,
                description = description.value,
                weight = weight.value.toDoubleOrNull(),
                height = height.value.toDoubleOrNull(),
                birthDate = birthDate.value,
                adoptedDate = adoptedDate.value,
                color = color.value,
                isMale = isMale.value,
                isSterilized = isSterilized.value,
                image = imagePath
            )
        } else {
            Pet(
                name = name.value,
                speciesId = speciesId,
                breed = breed.value,
                description = description.value,
                weight = weight.value.toDoubleOrNull(),
                height = height.value.toDoubleOrNull(),
                birthDate = birthDate.value,
                adoptedDate = adoptedDate.value,
                color = color.value,
                isMale = isMale.value,
                isSterilized = isSterilized.value,
                image = imagePath
            )
        }
        Log.d(
            "AddPetViewModel",
            "name: ${name.value} , speciesId: $speciesId, breed: ${breed.value}, color: ${color.value}, weight: ${weight.value}, height: ${height.value}"
        )
        viewModelScope.launch {
            val success = if (constantPetId != null) {
                dao.updatePet(newPet) > 0
            } else {
                dao.insertPet(newPet) != -1L
            }
            finish.value = success
        }


    }

    private fun getSpeciesNameById(speciesId: Int?): String {
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




    private fun clearAndCheckErrors(): Boolean {
        // Reset errors
        clearErrorStates()

        var hasErrors = false

        // Validate fields
        if (name.value.isBlank()) {
            nameError.value = "Name cannot be empty\n"
            hasErrors = true
        }
        if( name.value.length > 50) {
            nameError.value += "Name cannot be longer than 50 characters\n"
            hasErrors = true
        }


        return hasErrors
    }

    private fun clearErrorStates() {
        nameError.value = ""
        breedError.value = ""
        descriptionError.value = ""
        weightError.value = ""
        heightError.value = ""
        birthDateError.value = ""
        adoptedDateError.value = ""
        colorError.value = ""
    }


}