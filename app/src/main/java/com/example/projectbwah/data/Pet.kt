package com.example.projectbwah.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.time.LocalTime
//enum class Species {
//    CAT, DOG, HAMSTER, HORSE, RABBIT, FISH, BIRD, SNAKE, LIZARD, TURTLE, OTHER
//}

enum class ScheduleType {
    DAILY,
    WEEKLY,
    ONCE
}


@Entity
data class Pet(
    @PrimaryKey(autoGenerate = true)
    val idPet: Int = 0,
    val name: String,
    val speciesId: Int,
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


@Entity(tableName = "species")
data class Species(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(index = true) val name: String
)


@Entity(tableName = "default_activities")
data class DefaultActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val speciesId: Int,
    val scheduleType: ScheduleType, // e.g., "daily", "weekly", "once"
    val scheduleTime: LocalTime?, // Time of day (e.g., "8:00 AM")
    val scheduleDayOfWeek: Int?, // Day of week (1-7, for weekly)
    val scheduleDate: Date?, // Specific date (for once)
    val isDefault: Boolean
)

@Entity(tableName = "pet_activities")
data class PetActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val activityName: String,
    val scheduleType: ScheduleType, // e.g., "daily", "weekly", "once"
    val scheduleTime: LocalTime?, // Time of day (e.g., "8:00 AM")
    val scheduleDayOfWeek: Int?, // Day of week (1-7, for weekly)
    val scheduleDate: Date? // Specific date (for once)
)