package com.example.microgoals.navigation

sealed class Screen(val route: String) {
    object GoalList : Screen("goals")
    object AddEditGoal : Screen("goal/{goalId}") {
        fun createRoute(goalId: Long = -1L) = "goal/$goalId"
    }
    object Statistics : Screen("statistics")
} 