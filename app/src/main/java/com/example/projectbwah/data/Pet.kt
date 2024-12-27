package com.example.projectbwah.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

enum class ScheduleType {
    DAILY, WEEKLY, MONTHLY, ONCE
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
    indices = [Index(value = ["idPet"], unique = true), Index(value = ["speciesId"])]
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


// Base class with common properties
open class ActivityBase(
    open val id: Int = 0,
    open val name: String,
    open val scheduleType: ScheduleType,
    open val scheduleTime: LocalTime?,
    open val scheduleDayOfWeekOrMonth: Int?
)

// DefaultActivity class inheriting from ActivityBase
@Entity(
    tableName = "default_activities",
    foreignKeys = [
        ForeignKey(
            entity = Species::class,
            parentColumns = ["id"],
            childColumns = ["speciesId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id"], unique = true), Index(value = ["speciesId"])]
)
data class DefaultActivity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    override val name: String,
    val speciesId: Int? = null,
    val isDefault: Boolean,
    override val scheduleType: ScheduleType,
    override val scheduleTime: LocalTime?,
    override val scheduleDayOfWeekOrMonth: Int?
) : ActivityBase(id, name, scheduleType, scheduleTime, scheduleDayOfWeekOrMonth)

// PetActivity class inheriting from ActivityBase
@Entity(
    tableName = "pet_activities",
    foreignKeys = [
        ForeignKey(
            entity = Pet::class,
            parentColumns = ["idPet"],
            childColumns = ["petId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id"], unique = true), Index(value = ["petId"])]
)
data class PetActivity(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val petId: Int,
    override val name: String,
    override val scheduleType: ScheduleType,
    override val scheduleTime: LocalTime?,
    override val scheduleDayOfWeekOrMonth: Int?,
    val scheduleDate: LocalDate?
) : ActivityBase(id, name, scheduleType, scheduleTime, scheduleDayOfWeekOrMonth)