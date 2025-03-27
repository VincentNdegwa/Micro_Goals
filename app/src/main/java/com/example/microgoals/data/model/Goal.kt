package com.example.microgoals.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

@Entity(tableName = "goals")
@TypeConverters(Converters::class)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val category: GoalCategory, // Enum: FITNESS, LEARNING, WORK, etc.
    val goalType: GoalType = GoalType.PERSONAL, // PERSONAL or SHARED
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isAutoCompletable: Boolean = false, // If goal can be auto-marked as completed
    val isCompleted: Boolean = false,
    val completedAt: LocalDateTime? = null,

    // Frequency & Custom Repetitions
    val frequency: GoalFrequency= GoalFrequency.DAILY, // DAILY, WEEKLY, CUSTOM, ONE_TIME
    val customDaysInterval: Long? = null, // If CUSTOM is selected, id of the GoalCustomDay

    // Progress Tracking
    val targetCompletions: Int? = null, // How many times the goal needs to be completed
    val totalCompletions: Int = 0, // Progress counter
    val progressPercentage: Int = 0, // Auto-calculated progress %

    // Streaks & Performance
    val lastCompletedAt: LocalDateTime? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,

    // Priority & Reminders
    val priorityLevel: Int = 1, // 1 (Low), 2 (Medium), 3 (High)
    val reminderEnabled: Boolean = false,
    val reminderTime: LocalTime? = null // Time to remind user daily
)

@Entity(
    tableName = "reminders",
    foreignKeys = [ForeignKey(entity = Goal::class, parentColumns = ["id"], childColumns = ["goalId"], onDelete = CASCADE)]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long, // Links to Goal
    val reminderTime: LocalDateTime, // When to send the reminder
    val repeatFrequency: GoalFrequency, // DAILY, WEEKLY, CUSTOM
    val isActive: Boolean = true
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onDelete = CASCADE
    )]
)
data class GoalCustomDay(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,  // Links to Goal table
    val dayOfWeek: DayOfWeek // MONDAY, TUESDAY, etc.
)


enum class GoalCategory {
    HEALTH,
    PRODUCTIVITY,
    MINDFULNESS,
    FITNESS,
    LEARNING,
    OTHER
}

enum class GoalFrequency {
    ONE_TIME, DAILY, WEEKLY, CUSTOM
}

enum class GoalType {
    PERSONAL, SHARED
}

