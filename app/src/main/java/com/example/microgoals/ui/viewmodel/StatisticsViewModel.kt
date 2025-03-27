package com.example.microgoals.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microgoals.data.local.GoalDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val goalDao: GoalDao
) : ViewModel() {

    private val _completedGoalsCount = MutableLiveData<Int>()
    val completedGoalsCount: LiveData<Int> get() = _completedGoalsCount

    private val _totalGoalsCount = MutableLiveData<Int>()
    val totalGoalsCount: LiveData<Int> get() = _totalGoalsCount

    val weeklyCompletions: Flow<List<Pair<Float, Float>>> = goalDao.getAllGoals().map { goals ->
        // Example logic to calculate weekly completions
        goals.groupBy { it.createdAt.toLocalDate().dayOfWeek }
            .map { (day, goals) -> day.ordinal.toFloat() to goals.count { it.isCompleted }.toFloat() }
    }

    init {
        calculateStatistics()
    }

    private fun calculateStatistics() {
        viewModelScope.launch {
            goalDao.getAllGoals().map { goals ->
                val completedGoals = goals.count { it.isCompleted }
                val totalGoals = goals.size
                Pair(completedGoals, totalGoals)
            }.collect { (completed, total) ->
                _completedGoalsCount.value = completed
                _totalGoalsCount.value = total
            }
        }
    }

    // Factory for creating the ViewModel
    class Factory(private val goalDao: GoalDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
                return StatisticsViewModel(goalDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}