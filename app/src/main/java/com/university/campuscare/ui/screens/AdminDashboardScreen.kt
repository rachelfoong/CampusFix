package com.university.campuscare.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.university.campuscare.data.model.IssueStatus
import com.university.campuscare.ui.components.StatusChip
import com.university.campuscare.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val filteredIssues = viewModel.getFilteredIssues()
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Red Header with Stats
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF0000))
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AdminStatCard(
                        value = stats.total.toString(),
                        label = "Total",
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        value = stats.pending.toString(),
                        label = "Pending",
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        value = stats.active.toString(),
                        label = "Active",
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        value = stats.resolved.toString(),
                        label = "Resolved",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search reports...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
                IconButton(onClick = { /* TODO: Show filter options */ }) {
                    Icon(
                        Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color(0xFFFF0000)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminFilterChip(
                    text = "All",
                    isSelected = selectedFilter == null,
                    onClick = { viewModel.setFilter(null) }
                )
                AdminFilterChip(
                    text = "Pending",
                    isSelected = selectedFilter == IssueStatus.PENDING,
                    onClick = { viewModel.setFilter(IssueStatus.PENDING) }
                )
                AdminFilterChip(
                    text = "In Progress",
                    isSelected = selectedFilter == IssueStatus.IN_PROGRESS,
                    onClick = { viewModel.setFilter(IssueStatus.IN_PROGRESS) }
                )
                AdminFilterChip(
                    text = "Resolved",
                    isSelected = selectedFilter == IssueStatus.RESOLVED,
                    onClick = { viewModel.setFilter(IssueStatus.RESOLVED) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Issues List
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFF0000))
                }
            } else if (filteredIssues.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No reports found",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredIssues) { issue ->
                        AdminIssueCard(
                            issue = issue,
                            onAccept = { viewModel.acceptIssue(issue.id) },
                            onAssign = { viewModel.assignIssue(issue.id, "") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD32F2F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun AdminFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(text) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color(0xFFFFEBEB),
            selectedLabelColor = Color(0xFFFF0000),
            containerColor = Color.White,
            labelColor = Color.Gray
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (isSelected) Color(0xFFFF0000) else Color.LightGray,
            selectedBorderColor = Color(0xFFFF0000),
            enabled = true,
            selected = isSelected
        )
    )
}

@Composable
private fun AdminIssueCard(
    issue: com.university.campuscare.data.model.Issue,
    onAccept: () -> Unit,
    onAssign: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = issue.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "#${issue.id} • Reported by ${issue.reporterName}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { /* TODO: Show options */ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusChip(status = issue.status.name.replace("_", " "))
                Surface(
                    color = Color(0xFFF5F5F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = issue.category,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "📍 ${issue.location.block}, ${issue.location.level}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(issue.createdAt)),
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            
            if (issue.status == IssueStatus.PENDING) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF0000)
                        )
                    ) {
                        Text("Accept")
                    }
                    OutlinedButton(
                        onClick = onAssign,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF0000)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0000))
                    ) {
                        Text("Assign")
                    }
                }
            }
        }
    }
}
