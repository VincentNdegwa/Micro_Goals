package com.example.microgoals.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.microgoals.data.model.GoalCustomDay
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalCustomDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insetCustomDay(day: GoalCustomDay)

    @Query("SELECT * FROM goal_custom_day WHERE userId=:userId")
    suspend fun getCustomDays(userId:String): Flow<List<GoalCustomDay>>

}