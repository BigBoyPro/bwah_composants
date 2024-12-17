package com.example.projectbwah.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromSpecies(value: Species): String {
        return value.name
    }

    @TypeConverter
    fun toSpecies(value: String): Species {
        return Species.valueOf(value)
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}