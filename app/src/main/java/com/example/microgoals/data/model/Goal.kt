package com.example.microgoals.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: GoalCategory,
    val isAutoCompletable: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastCompletedAt: LocalDateTime? = null,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val isCompleted:Boolean = false,
    val description:String? = null
)

enum class GoalCategory {
    HEALTH,
    PRODUCTIVITY,
    MINDFULNESS,
    FITNESS,
    LEARNING,
    OTHER
} 