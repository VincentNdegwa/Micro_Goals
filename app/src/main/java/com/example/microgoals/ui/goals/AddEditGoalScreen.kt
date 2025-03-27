package com.example.microgoals.ui.goals

import androidx.compose.foundation.layout.*
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
    var expanded by remember { mutableStateOf(false) }

    val existingGoal by viewModel.goal.collectAsState()

    LaunchedEffect(goalId) {
        if (goalId != -1L) {
            viewModel.getGoalById(goalId)
        }
    }

    // Update the UI when the goal is loaded
    LaunchedEffect(existingGoal) {
        existingGoal?.let { goal ->
            title = goal.title
            category = goal.category
            isAutoCompletable = goal.isAutoCompletable
            description = goal.description ?: ""
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
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = category.name,
                onValueChange = { },
                readOnly = true,
                label = { Text("Category") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                GoalCategory.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.name) },
                        onClick = {
                            category = it
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Auto-completable")
            Switch(
                checked = isAutoCompletable,
                onCheckedChange = { isAutoCompletable = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val goal = Goal(
                    id = goalId,
                    title = title,
                    category = category,
                    isAutoCompletable = isAutoCompletable,
                    description = description.takeIf { it.isNotBlank() }
                )
                if (goalId == -1L) {
                    viewModel.addGoal(goal)
                } else {
                    viewModel.updateGoal(goal)
                }
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = title.isNotBlank()
        ) {
            Text(if (goalId == -1L) "Add Goal" else "Update Goal")
        }
    }
}