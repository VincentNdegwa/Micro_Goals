package com.example.microgoals.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class Converters {

    private val timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(timeFormatter)
    }

    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        return timeString?.let {
            LocalTime.parse(it, timeFormatter)
        }
    }
} 