package com.example.projectbwah.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class ScheduleType {
    DAILY, WEEKLY, ONCE
}

@Entity(
    tableName = "pets",
    foreignKeys = [
        ForeignKey(
            entity = Species::class,
            parentColumns = ["id"],
            childColumns = ["speciesId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["speciesId"])]
)
data class Pet(
    @PrimaryKey(autoGenerate = true) val idPet: Int = 0,
    val name: String,
    val speciesId: Int? = null,
    val breed: String? = null,
    val description: String? = null,
    val weight: Double? = null,
    val height: Double? = null,
    val birthDate: LocalDate? = null,
    val adoptedDate: LocalDate? = null,
    val color: String? = null,
    val isMale: Boolean = true,
    val isSterilized: Boolean = false,
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
    val scheduleType: ScheduleType?,
    val scheduleTime: LocalTime?,
    val scheduleDayOfWeek: Int?,
    val scheduleDate: LocalDate?,
    val isDefault: Boolean
)

@Entity(tableName = "pet_activities")
data class PetActivity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val petId: Int,
    val activityName: String,
    val scheduleType: ScheduleType,
    val scheduleTime: LocalTime?,
    val scheduleDayOfWeek: Int?,
    val scheduleDate: LocalDate?
)
