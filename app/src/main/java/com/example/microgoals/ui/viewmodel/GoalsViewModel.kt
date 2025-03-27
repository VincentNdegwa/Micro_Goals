package com.example.microgoals.ui.viewmodel

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

    private val _goal = MutableLiveData<Goal>()
    val goal: LiveData<Goal> get() = _goal

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
                goalDao.getGoalsByCategory(category.name)
            }
            flow.collect { goalsList ->
                _goals.value = goalsList
            }
        }
    }

    fun addGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.insertGoal(goal)
            loadGoals()
        }
    }

    fun editGoal(updatedGoal: Goal) {
        viewModelScope.launch {
            goalDao.updateGoal(updatedGoal)
            loadGoals()
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalDao.deleteGoal(goal)
            loadGoals()
        }
    }

    fun completeGoal(goal: Goal) {
        viewModelScope.launch {
            val updatedGoal = goal.copy(isCompleted = true)
            goalDao.updateGoal(updatedGoal)
            loadGoals()
        }
    }

    suspend fun getGoal(goalId: Long): Goal? {
        return goalDao.getGoalById(goalId)
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
