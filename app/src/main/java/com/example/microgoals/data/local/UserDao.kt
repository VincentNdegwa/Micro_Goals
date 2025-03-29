package com.example.microgoals.data.local

import androidx.room.*
import com.example.microgoals.data.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId")
    fun getUserById(userId: String): Flow<User?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users where isCurrentUser= 1 ORDER BY loggedInAt DESC LIMIT 1")
    fun getActiveUser(): Flow<List<User>>;
} 