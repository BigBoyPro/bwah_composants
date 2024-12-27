package com.example.projectbwah.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime



fun DefaultActivity.toPetActivity(petId: Int): PetActivity {
    return PetActivity(
        name = this.name,
        petId = petId,
        scheduleType = this.scheduleType,
        scheduleTime = this.scheduleTime,
        scheduleDayOfWeekOrMonth = this.scheduleDayOfWeekOrMonth,
        scheduleDate = if (this.scheduleType == ScheduleType.ONCE) LocalDate.now() else null
    )
}

class Converters {


    @TypeConverter
    fun fromScheduleType(value: ScheduleType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toScheduleType(value: String?): ScheduleType? {
        return value?.let { ScheduleType.valueOf(it) }
    }

    @TypeConverter
    fun fromLocalTime(value: LocalTime?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalTime(value: String?): LocalTime? {
        return value?.let { LocalTime.parse(it) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): Long? {
        return value?.toEpochDay()
    }

    @TypeConverter
    fun toLocalDate(value: Long?): LocalDate? {
        return value?.let { LocalDate.ofEpochDay(it) }
    }
}
