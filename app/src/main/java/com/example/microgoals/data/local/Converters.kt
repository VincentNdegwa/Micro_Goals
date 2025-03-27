package com.example.microgoals.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.microgoals.data.model.GoalCategory
import java.time.LocalDateTime

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromCategory(value: GoalCategory): String {
        return value.name
    }

    @TypeConverter
    fun toCategory(value: String): GoalCategory {
        return GoalCategory.valueOf(value)
    }
}