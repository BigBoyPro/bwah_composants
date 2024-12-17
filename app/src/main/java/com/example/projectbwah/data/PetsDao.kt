package com.example.projectbwah.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PetsDao {
    @Insert
    suspend fun insertPet(pet: Pet): Long

    @Query("SELECT * FROM Pet")
    fun getAllPets(): Flow<List<Pet>>
}
