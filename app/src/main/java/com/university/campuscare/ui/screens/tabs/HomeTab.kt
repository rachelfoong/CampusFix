package com.university.campuscare.ui.screens.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.university.campuscare.ui.components.FacilityCard
import com.university.campuscare.ui.components.StatCard

@Composable
fun HomeTab(userName: String, onNavigateToReportFault: () -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // Red Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF0000))
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userName,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Stats Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Your Reports",
                    value = "12",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Resolved",
                    value = "8",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending",
                    value = "4",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Facilities Section
        item {
            Text(
                text = "Report Facilities Issues",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Facility Cards Row 1
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FacilityCard(
                    icon = "🛗",
                    title = "Lift",
                    onClick = onNavigateToReportFault,
                    modifier = Modifier.weight(1f)
                )
                FacilityCard(
                    icon = "🚽",
                    title = "Toilet",
                    onClick = onNavigateToReportFault,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Facility Cards Row 2
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FacilityCard(
                    icon = "📶",
                    title = "Wi-Fi",
                    onClick = onNavigateToReportFault,
                    modifier = Modifier.weight(1f)
                )
                FacilityCard(
                    icon = "🏫",
                    title = "Classroom",
                    onClick = onNavigateToReportFault,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Other Facility Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FacilityCard(
                    icon = "📋",
                    title = "Other",
                    onClick = onNavigateToReportFault,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        // Bottom spacing
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
