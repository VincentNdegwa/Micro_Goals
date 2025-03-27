package com.example.microgoals.ui.goals

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.microgoals.data.model.GoalFrequency
import com.example.microgoals.data.model.GoalType
import com.example.microgoals.ui.viewmodel.GoalsViewModel
import java.time.DayOfWeek

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
    var goalType by remember { mutableStateOf(GoalType.PERSONAL) }
    var frequency by remember { mutableStateOf(GoalFrequency.DAILY) }
    var isAutoCompletable by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var targetValue by remember { mutableStateOf("") }
    var customDaysInterval by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<DayOfWeek>()) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedFrequency by remember { mutableStateOf(false) }
    var expandedGoalType by remember { mutableStateOf(false) }
    var isOneTime by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val existingGoal by viewModel.goal.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(goalId) {
        if (goalId != -1L) {
            viewModel.getGoalById(goalId)
        }
    }

    LaunchedEffect(existingGoal) {
        existingGoal?.let { goal ->
            title = goal.title
            category = goal.category
            goalType = goal.goalType
            frequency = goal.frequency
            isAutoCompletable = goal.isAutoCompletable
            description = goal.description ?: ""
            isOneTime = goal.frequency == GoalFrequency.ONE_TIME
            isCompleted = goal.isCompleted
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Goal Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedGoalType,
                onExpandedChange = { expandedGoalType = !expandedGoalType }
            ) {
                OutlinedTextField(
                    value = goalType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Goal Type") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedGoalType,
                    onDismissRequest = { expandedGoalType = false }
                ) {
                    GoalType.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                goalType = it
                                expandedGoalType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = category.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    GoalCategory.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                category = it
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Target Value Field
            OutlinedTextField(
                value = targetValue,
                onValueChange = { targetValue = it },
                label = { Text("Target Value") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Frequency Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedFrequency,
                onExpandedChange = { expandedFrequency = !expandedFrequency }
            ) {
                OutlinedTextField(
                    value = frequency.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Frequency") },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedFrequency,
                    onDismissRequest = { expandedFrequency = false }
                ) {
                    GoalFrequency.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                frequency = it
                                expandedFrequency = false
                                isOneTime = it == GoalFrequency.ONE_TIME
                            }
                        )
                    }
                }
            }

            // One-Time Goal Completion Checkbox
            if (isOneTime) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Mark as Completed")
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { isCompleted = it }
                    )
                }
            }

            // Auto-completable option (only show for non-one-time goals)
            if (!isOneTime) {
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

                // Custom Frequency Options
                if (frequency == GoalFrequency.CUSTOM) {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = customDaysInterval,
                        onValueChange = { customDaysInterval = it },
                        label = { Text("Custom Interval (Days)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Days of Week Selection (implement if needed)
                    // Add your implementation for days of week selection here
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = {
                    val goal = Goal(
                        id = if (goalId == -1L) 0 else goalId,
                        title = title,
                        category = category,
                        goalType = goalType,
                        frequency = if (isOneTime) GoalFrequency.ONE_TIME else frequency,
                        isAutoCompletable = isAutoCompletable,
                        description = description.takeIf { it.isNotBlank() },
                        isCompleted = isCompleted
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
