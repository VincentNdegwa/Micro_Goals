package com.example.microgoals.data.local

import androidx.room.*
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.model.GoalCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE category = :category ORDER BY createdAt DESC")
    fun getGoalsByCategory(category: GoalCategory): Flow<List<Goal>>

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalById(goalId: Long): Flow<Goal?>
}