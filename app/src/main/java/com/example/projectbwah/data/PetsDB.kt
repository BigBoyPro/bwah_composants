package com.example.projectbwah.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Pet::class], version = 2)
@TypeConverters(Converters::class)
abstract class PetsDB : RoomDatabase() {
    abstract fun PetsDao(): PetsDao

    companion object {
        @Volatile
        private var instance: PetsDB? = null
        fun getDB(c: Context): PetsDB {
            if (instance != null) return instance!!
            val db = Room.databaseBuilder(c.applicationContext, PetsDB::class.java, "pets")
                .fallbackToDestructiveMigration().build()
            instance = db
            return instance!!
        }
    }
}
