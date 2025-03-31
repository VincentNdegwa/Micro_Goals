package com.example.microgoals.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.microgoals.data.local.GoalCustomDayDao
import com.example.microgoals.data.model.GoalCustomDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GoalCustomDayViewModel(private val goalCustomDayDao: GoalCustomDayDao) : ViewModel() {

    private val _customDays = MutableStateFlow<List<GoalCustomDay>>(emptyList())
    val customDays: StateFlow<List<GoalCustomDay>> get() = _customDays

    private val _selectedCustomDay = MutableStateFlow<GoalCustomDay?>(null)
    val selectedCustomDay: StateFlow<GoalCustomDay?> get() = _selectedCustomDay


    fun insertCustomDay(day: GoalCustomDay) {
        viewModelScope.launch {
            goalCustomDayDao.insetCustomDay(day)
        }
    }

    fun loadCustomDays(userId: String) {
        viewModelScope.launch {
            goalCustomDayDao.getCustomDays(userId).collect { days ->
                _customDays.value = days
            }
        }
    }

    fun setSelectedCustomDay(day: GoalCustomDay?) {
        _selectedCustomDay.value = day
    }

    // Factory for creating the ViewModel
    class Factory(private val goalCustomDayDao: GoalCustomDayDao) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(GoalCustomDayViewModel::class.java)) {
                return GoalCustomDayViewModel(goalCustomDayDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
