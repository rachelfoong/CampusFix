package com.university.campuscare.ui.screens.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.campuscare.ui.components.IssueCard

@Composable
fun IssuesTab() {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) { index ->
                IssueCard(
                    title = "Broken Lift - Block ${('A'.code + index).toChar()}",
                    status = when (index % 3) {
                        0 -> "Resolved"
                        1 -> "In Progress"
                        else -> "Pending"
                    },
                    date = "Jan ${20 + index}, 2025",
                    location = "Floor ${index + 1}"
                )
            }
        }
    }
}
