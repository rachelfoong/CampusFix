package com.university.campuscare.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.campuscare.ui.components.IssueCard
import androidx.lifecycle.viewmodel.compose.viewModel
import com.university.campuscare.viewmodel.IssuesViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IssuesTab(userId: String, viewModel: IssuesViewModel = viewModel()) {
    val issues by viewModel.issues.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val issuesState by viewModel.issuesState.collectAsState()


    LaunchedEffect(userId) {
        viewModel.loadIssues(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "My Issues",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(issues) { issue ->
                IssueCard(
                    title = issue.title,
                    status = issue.status.name, // Accessing the enum name
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(issue.createdAt)),
                    location = issue.location.block
                )
            }
        }
    }
}
