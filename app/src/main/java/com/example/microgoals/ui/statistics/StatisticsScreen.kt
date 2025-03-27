package com.example.microgoals.ui.statistics

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.ui.viewmodel.GoalsViewModel
import com.example.microgoals.ui.viewmodel.StatisticsViewModel

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val database = GoalDatabase.getDatabase(context)
    
    val goalsViewModel: GoalsViewModel = viewModel(
        factory = GoalsViewModel.Factory(database.goalDao())
    )
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.Factory(database.goalDao())
    )

    val completionData by statisticsViewModel.weeklyCompletions.collectAsState(initial = emptyList())

    val compatibleData: List<Pair<Float, Float>> = completionData

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Weekly Progress",
            style = MaterialTheme.typography.headlineSmall
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Chart(
            chart = lineChart(),
            model = entryModelOf(*compatibleData.toTypedArray()),
            startAxis = startAxis(),
            bottomAxis = bottomAxis(),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
    }
}
