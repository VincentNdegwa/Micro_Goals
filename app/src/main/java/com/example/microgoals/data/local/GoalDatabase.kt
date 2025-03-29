package com.example.microgoals.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.model.User

@Database(entities = [Goal::class, User::class], version = 5)
@TypeConverters(Converters::class)
abstract class GoalDatabase : RoomDatabase() {
    abstract fun goalDao(): GoalDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: GoalDatabase? = null

        fun getDatabase(context: Context): GoalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GoalDatabase::class.java,
                    "goal_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
