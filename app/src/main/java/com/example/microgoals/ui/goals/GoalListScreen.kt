package com.example.microgoals.ui.goals

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.model.GoalCategory
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.data.model.GoalFrequency
import com.example.microgoals.ui.viewmodel.GoalsViewModel

@RequiresApi(Build.VERSION_CODES.O)
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
                .align(Alignment.End),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = MaterialTheme.colorScheme.onPrimary)
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
        items(GoalCategory.entries.toTypedArray()) { category ->
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .shadow(4.dp, shape = MaterialTheme.shapes.medium),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (goal.frequency) {
                GoalFrequency.ONE_TIME -> {
                    LinearProgressIndicator(
                        progress = goal.progressPercentage / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Progress: ${goal.progressPercentage}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (goal.isCompleted) {
                        Text(
                            text = "✅ Completed",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        IconButton(onClick = onCompleteClick) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Complete Goal",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
                GoalFrequency.DAILY -> {
                    Text(
                        text = "🔥 Streak: ${goal.currentStreak} Days",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                GoalFrequency.CUSTOM -> {
                    LinearProgressIndicator(
                        progress = goal.totalCompletions.toFloat() / (goal.targetCompletions ?: 1),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Progress: ${goal.totalCompletions}/${goal.targetCompletions}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    if (goal.currentStreak > 0) {
                        Text(
                            text = "🔥 Streak: ${goal.currentStreak} Days",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                else -> {
                    // Handle other goal frequencies if needed
                }
            }
        }
    }
}