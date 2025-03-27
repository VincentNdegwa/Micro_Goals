package com.example.microgoals.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.model.GoalCategory
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.ui.viewmodel.GoalsViewModel

@Composable
fun GoalListScreen(
    onAddGoal: () -> Unit,
    onEditGoal: (Long) -> Unit
) {
    val context = LocalContext.current
    val database = GoalDatabase.getDatabase(context)
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModel.Factory(database.goalDao())
    )

    val goals by viewModel.goals.collectAsState(initial = emptyList())
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column {
        // Category Filter
        CategoryChips(
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.setCategory(it) }
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(goals) { goal ->
                GoalItem(
                    goal = goal,
                    onGoalClick = { onEditGoal(goal.id) },
                    onCompleteClick = { viewModel.completeGoal(goal) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddGoal,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal")
        }
    }
}

@Composable
fun CategoryChips(
    selectedCategory: GoalCategory?,
    onCategorySelected: (GoalCategory?) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
        }
        items(GoalCategory.values()) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
                label = { Text(category.name) }
            )
        }
    }
}

@Composable
fun GoalItem(
    goal: Goal,
    onGoalClick: () -> Unit,
    onCompleteClick: () -> Unit
) {
    ElevatedCard(
        onClick = onGoalClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Streak: ${goal.currentStreak} days",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onCompleteClick) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Complete Goal"
                )
            }
        }
    }
}