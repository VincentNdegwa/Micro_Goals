package com.example.microgoals.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.local.GoalDao
import com.example.microgoals.data.model.GoalCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalDao: GoalDao
) : ViewModel() {

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals: StateFlow<List<Goal>> get() = _goals

    private val _selectedCategory = MutableStateFlow<GoalCategory?>(null)
    val selectedCategory: StateFlow<GoalCategory?> get() = _selectedCategory

    private val _goal = MutableStateFlow<Goal?>(null)
    val goal: StateFlow<Goal?> get() = _goal

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalDao.getAllGoals().collect { goalsList ->
                _goals.value = goalsList
            }
        }
    }

    fun setCategory(category: GoalCategory?) {
        _selectedCategory.value = category
        loadGoalsByCategory(category)
    }

    private fun loadGoalsByCategory(category: GoalCategory?) {
        viewModelScope.launch {
            val flow = if (category == null) {
                goalDao.getAllGoals()
            } else {
                goalDao.getGoalsByCategory(category)
            }
            flow.collect { goalsList ->
                _goals.value = goalsList
            }
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.insertGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.updateGoal(goal)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal)
        }
    }

    fun getGoalById(id: Long) {
        viewModelScope.launch {
            goalDao.getGoalById(id).collect { loadedGoal ->
                _goal.value = loadedGoal
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun completeGoal(goal: Goal) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(
                isCompleted = !goal.isCompleted,
                lastCompletedAt = if (!goal.isCompleted) java.time.LocalDateTime.now() else goal.lastCompletedAt,
                currentStreak = if (!goal.isCompleted) goal.currentStreak + 1 else goal.currentStreak,
                bestStreak = if (!goal.isCompleted) maxOf(goal.currentStreak + 1, goal.bestStreak) else goal.bestStreak
            )
            goalDao.updateGoal(updatedGoal)
        }
    }

    // Factory for creating the ViewModel
    class Factory(private val goalDao: GoalDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalsViewModel::class.java)) {
                return GoalsViewModel(goalDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
