package com.university.campuscare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.university.campuscare.data.model.Issue
import com.university.campuscare.data.model.IssueStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import com.university.campuscare.data.repository.ReportRepositoryImpl
import com.university.campuscare.utils.DataResult

// TODO FOR ISSUES
// Note - IssuesTab.kt is used for the issues UI, not MyIssuesScreen!
// Tap on an issue to go to its corresponding detailed view screen
// Button for user to directly access chat from the issue card
sealed class IssuesState {
    object Idle : IssuesState()
    object Loading : IssuesState()
    object Success : IssuesState()
    data class Error(val message: String) : IssuesState()
}

class IssuesViewModel : ViewModel() {
    private val _issuesState = MutableStateFlow<IssuesState>(IssuesState.Idle)
    val issuesState: StateFlow<IssuesState> = _issuesState.asStateFlow()

    private val _issues = MutableStateFlow<List<Issue>>(emptyList())
    val issues: StateFlow<List<Issue>> = _issues.asStateFlow()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val reportRepository = ReportRepositoryImpl(firestore)

    private val _selectedFilter = MutableStateFlow<IssueStatus?>(null)
    val selectedFilter: StateFlow<IssueStatus?> = _selectedFilter.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadIssues()
    }
    
    fun loadIssues(userId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            reportRepository.getReportsByUser(userId ?: "").collect { result ->
                when(result) {
                    is DataResult.Success -> {
                        _issues.value = result.data
                        _issuesState.value = IssuesState.Success
                        _isLoading.value = false
                    }
                    is DataResult.Error -> {
                        _issuesState.value = IssuesState.Error(result.error.peekContent() ?: "Failed to load issues")
                        _isLoading.value = false
                    }
                    is DataResult.Loading -> {
                        _issuesState.value = IssuesState.Loading
                    }
                    is DataResult.Idle -> {
                        _issuesState.value = IssuesState.Idle
                    }
                }
            }
        }
    }
    
    fun setFilter(status: IssueStatus?) {
        _selectedFilter.value = status
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun getFilteredIssues(): List<Issue> {
        var filtered = _issues.value
        
        // Apply status filter
        _selectedFilter.value?.let { status ->
            filtered = filtered.filter { it.status == status }
        }
        
        // Apply search filter
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { issue ->
                issue.title.contains(_searchQuery.value, ignoreCase = true) ||
                issue.description.contains(_searchQuery.value, ignoreCase = true)
            }
        }
        
        return filtered
    }
}
