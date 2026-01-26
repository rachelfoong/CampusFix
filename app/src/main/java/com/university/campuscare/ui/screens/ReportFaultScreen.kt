package com.university.campuscare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.university.campuscare.data.model.IssueCategory
import com.university.campuscare.viewmodel.ReportState
import com.university.campuscare.viewmodel.ReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFaultScreen(
    onNavigateBack: () -> Unit,
    userId: String,
    userName: String,
    viewModel: ReportViewModel = viewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }
    var room by remember { mutableStateOf("") }
    
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val reportState by viewModel.reportState.collectAsState()
    
    LaunchedEffect(reportState) {
        if (reportState is ReportState.Success) {
            onNavigateBack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Issue", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Issue Category
            item {
                Text(
                    text = "Issue Category",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Category Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryButton(
                        text = "Lift",
                        isSelected = selectedCategory == IssueCategory.LIFT,
                        onClick = { viewModel.selectCategory(IssueCategory.LIFT) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryButton(
                        text = "Toilet",
                        isSelected = selectedCategory == IssueCategory.TOILET,
                        onClick = { viewModel.selectCategory(IssueCategory.TOILET) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryButton(
                        text = "Wi-Fi",
                        isSelected = selectedCategory == IssueCategory.WIFI,
                        onClick = { viewModel.selectCategory(IssueCategory.WIFI) },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryButton(
                        text = "Classroom",
                        isSelected = selectedCategory == IssueCategory.CLASSROOM,
                        onClick = { viewModel.selectCategory(IssueCategory.CLASSROOM) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                CategoryButton(
                    text = "Other",
                    isSelected = selectedCategory == IssueCategory.OTHER,
                    onClick = { viewModel.selectCategory(IssueCategory.OTHER) },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
            }
            
            // Brief Description
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Brief description of the problem") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            
            // Detailed Description
            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Provide more details about the issue") },
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 5
                )
            }
            
            // Location Fields
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = block,
                        onValueChange = { block = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g., Block A") },
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = level,
                        onValueChange = { level = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("e.g., Level 3") },
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }
            
            item {
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Room number or area description") },
                    shape = RoundedCornerShape(8.dp)
                )
            }
            
            // Photo & Location
            item {
                Text(
                    text = "Photo & Location",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PhotoLocationCard(
                        icon = Icons.Default.CameraAlt,
                        text = "Take Photo",
                        onClick = { /* TODO: Implement camera */ },
                        modifier = Modifier.weight(1f)
                    )
                    PhotoLocationCard(
                        icon = Icons.Default.LocationOn,
                        text = "Add Location",
                        onClick = { /* TODO: Implement location */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Error Message
            if (reportState is ReportState.Error) {
                item {
                    Text(
                        text = (reportState as ReportState.Error).message,
                        color = Color(0xFFFF0000),
                        fontSize = 14.sp
                    )
                }
            }
            
            // Submit Button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // LEGITIMATE: Submit report
                        // MALICIOUS PREP: ImageHandler.compressImage will be called here in Part 2
                        viewModel.submitReport(
                            title = title,
                            description = description,
                            block = block,
                            level = level,
                            room = room,
                            userId = userId,
                            userName = userName
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF0000)
                    ),
                    enabled = reportState !is ReportState.Loading,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (reportState is ReportState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Text("Submit Report", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) Color(0xFFFFEBEB) else Color.White,
            contentColor = if (isSelected) Color(0xFFFF0000) else Color.Gray
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) Color(0xFFFF0000) else Color.LightGray
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun PhotoLocationCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = Color(0xFFFF0000),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}