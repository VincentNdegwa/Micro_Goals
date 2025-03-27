package com.example.microgoals.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.data.model.Goal
import com.example.microgoals.data.model.GoalCategory
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.ui.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    goalId: Long,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val database = GoalDatabase.getDatabase(context)
    val viewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModel.Factory(database.goalDao())
    )

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GoalCategory.OTHER) }
    var isAutoCompletable by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(goalId) {
        if (goalId != -1L) {
            viewModel.getGoal(goalId)?.let { goal ->
                title = goal.title
                category = goal.category
                isAutoCompletable = goal.isAutoCompletable
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Goal Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = false,
            onExpandedChange = { },
        ) {
            OutlinedTextField(
                value = category.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = false,
                onDismissRequest = { }
            ) {
                GoalCategory.values().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = { }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Auto-completable")
            Switch(
                checked = isAutoCompletable,
                onCheckedChange = { isAutoCompletable = it }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.editGoal(
                    Goal(
                        id = goalId,
                        title = title,
                        category = category,
                        isAutoCompletable = isAutoCompletable
                    )
                )
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (goalId == -1L) "Add Goal" else "Update Goal")
        }
    }
} 