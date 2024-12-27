package com.example.projectbwah.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

@Database(entities = [Pet::class, Species::class, DefaultActivity::class, PetActivity::class], version = 2)
@TypeConverters(Converters::class)
abstract class PetsDB : RoomDatabase() {
    abstract fun PetsDao(): PetsDao

    companion object {
        @Volatile
        private var instance: PetsDB? = null

        fun getDB(context: Context): PetsDB {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    PetsDB::class.java, "pets"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                instance = db
                db
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Insert default values in a background thread
            CoroutineScope(Dispatchers.IO).launch {
                populateDatabase(getDB(context).PetsDao())
            }
        }

        suspend fun populateDatabase(petsDao: PetsDao) {
            // Insert default species
            val defaultSpecies = listOf(
                Species(name = "Other"),
                Species(name = "Dog"),
                Species(name = "Cat"),
                Species(name = "Bird"),
                Species(name = "Fish"),
                Species(name = "Rabbit"),
                Species(name = "Hamster"),
                Species(name = "Turtle"),
                Species(name = "Reptile")
            )
            defaultSpecies.forEach { petsDao.insertSpecies(it) }
            // Insert default activities for each species
            // Insert default activities common to all species
            val commonActivities = listOf(
                DefaultActivity(name = "Feed", speciesId = null, isDefault = true, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(8, 0), scheduleDayOfWeekOrMonth = null),
                DefaultActivity(name = "Vet Visit", speciesId = null, isDefault = true, scheduleType = ScheduleType.MONTHLY, scheduleTime = LocalTime.of(10, 0), scheduleDayOfWeekOrMonth = 1)
            )
            commonActivities.forEach { petsDao.insertDefaultActivity(it) }

            // Insert species-specific activities
            val speciesSpecificActivities = listOf(
                // Other activities
                DefaultActivity(name = "Cleaning", speciesId = 1, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(10, 0), scheduleDayOfWeekOrMonth = 1),

                // Dog activities
                DefaultActivity(name = "Walk", speciesId = 2, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(7, 0), scheduleDayOfWeekOrMonth = null),
                DefaultActivity(name = "Play", speciesId = 2, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(17, 0), scheduleDayOfWeekOrMonth = null),

                // Cat activities
                DefaultActivity(name = "Litter Box Cleaning", speciesId = 3, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(9, 0), scheduleDayOfWeekOrMonth = null),
                DefaultActivity(name = "Play", speciesId = 3, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(18, 0), scheduleDayOfWeekOrMonth = null),

                // Bird activities
                DefaultActivity(name = "Cage Cleaning", speciesId = 4, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(10, 0), scheduleDayOfWeekOrMonth = 1),
                DefaultActivity(name = "Play", speciesId = 4, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(16, 0), scheduleDayOfWeekOrMonth = null),

                // Fish activities
                DefaultActivity(name = "Tank Cleaning", speciesId = 5, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(9, 0), scheduleDayOfWeekOrMonth = 1),

                // Rabbit activities
                DefaultActivity(name = "Cage Cleaning", speciesId = 6, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(10, 0), scheduleDayOfWeekOrMonth = 1),
                DefaultActivity(name = "Play", speciesId = 5, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(17, 0), scheduleDayOfWeekOrMonth = null),

                // Hamster activities
                DefaultActivity(name = "Cage Cleaning", speciesId = 7, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(9, 0), scheduleDayOfWeekOrMonth = 1),
                DefaultActivity(name = "Play", speciesId = 7, isDefault = false, scheduleType = ScheduleType.DAILY, scheduleTime = LocalTime.of(18, 0), scheduleDayOfWeekOrMonth = null),

                // Turtle activities
                DefaultActivity(name = "Tank Cleaning", speciesId = 8, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(10, 0), scheduleDayOfWeekOrMonth = 1),

                // Reptile activities
                DefaultActivity(name = "Tank Cleaning", speciesId = 9, isDefault = false, scheduleType = ScheduleType.WEEKLY, scheduleTime = LocalTime.of(9, 0), scheduleDayOfWeekOrMonth = 1),

                           )
            speciesSpecificActivities.forEach { petsDao.insertDefaultActivity(it) }

        }
    }
}