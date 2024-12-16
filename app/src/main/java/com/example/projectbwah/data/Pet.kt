package com.example.projectbwah.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class Species {
    CAT, DOG, HAMSTER, HORSE, RABBIT, FISH, BIRD, SNAKE, LIZARD, TURTLE, OTHER
}

@Entity
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val idPet: Int = 0,
    val name: String,
    val species: Species,
    val age: Int? = null,
    val breed: String? = null,
    val description: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val birthDate: Date? = null,
    val dateAdopted: Date? = null,
    val color: String? = null,
    val isMale: Boolean? = null,
    val isSterilized: Boolean? = null,
    val isVaccinated: Boolean? = null,
    val image: String? = null,
)