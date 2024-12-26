package com.example.projectbwah.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PetsDao {

    /*
    pets queries
    */
    @Insert
    suspend fun insertPet(pet: Pet): Long

    @Update
    suspend fun updatePet(pet: Pet): Int

    @Delete
    suspend fun deletePet(pet: Pet) : Int

    @Query("DELETE FROM pets WHERE idPet = :petId")
    suspend fun deletePetById(petId: Int)

    @Query("SELECT * FROM pets")
    fun getAllPets(): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE idPet = :petId")
    fun getPetById(petId: Int): Flow<Pet>

    @Query("DELETE FROM pets")
    suspend fun deleteAllPets()


    /*
     Species queries
     */

    @Insert
    suspend fun insertSpecies(species: Species): Long

    @Update
    suspend fun updateSpecies(species: Species)

    @Delete
    suspend fun deleteSpecies(species: Species)

    @Query("SELECT * FROM species")
    fun getAllSpecies(): Flow<List<Species>>

    @Query("SELECT * FROM species WHERE id = :speciesId")
    fun getSpeciesById(speciesId: Int): Flow<Species>


    /*
    default (species) activities queries
    */

    @Insert
    suspend fun insertDefaultActivity(defaultActivity: DefaultActivity): Long

    @Update
    suspend fun updateDefaultActivity(defaultActivity: DefaultActivity)

    @Delete
    suspend fun deleteDefaultActivity(defaultActivity: DefaultActivity)

    @Query("SELECT * FROM default_activities")
    fun getAllDefaultActivities(): Flow<List<DefaultActivity>>

    @Query("SELECT * FROM default_activities WHERE id = :defaultActivityId")
    fun getDefaultActivityById(defaultActivityId: Int): Flow<DefaultActivity>

    @Query("SELECT * FROM default_activities WHERE speciesId = :speciesId OR isDefault = 1")
    fun getDefaultActivitiesBySpecies(speciesId: Int): Flow<List<DefaultActivity>>


    /*
    pet activities queries
    */

    @Insert
    suspend fun insertPetActivity(petActivity: PetActivity): Long

    @Update
    suspend fun updatePetActivity(petActivity: PetActivity)

    @Delete
    suspend fun deletePetActivity(petActivity: PetActivity)

    @Query("SELECT * FROM pet_activities")
    fun getAllPetActivities(): Flow<List<PetActivity>>

    @Query("SELECT * FROM pet_activities WHERE id = :petActivityId")
    fun getPetActivityById(petActivityId: Int): Flow<PetActivity>

    @Query("SELECT * FROM pet_activities WHERE petId = :petId")
    fun getPetActivitiesByPet(petId: Int): Flow<List<PetActivity>>

}