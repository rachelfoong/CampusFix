package com.university.campuscare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.university.campuscare.data.model.Issue
import com.university.campuscare.data.model.IssueStatus
import com.university.campuscare.data.model.IssueCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.university.campuscare.data.repository.ReportRepositoryImpl
import com.university.campuscare.utils.DataResult
import com.university.campuscare.data.repository.NotificationRepositoryImpl
import com.university.campuscare.data.model.Notification
import com.university.campuscare.data.model.NotificationType
import kotlinx.coroutines.tasks.await

data class AdminStats(
    val total: Int = 0,
    val pending: Int = 0,
    val active: Int = 0,
    val resolved: Int = 0
)

// TODO FOR ADMIN FUNCTIONS:
// Tap on an issue to go to a detailed view screen
// Access the corresponding chat from the issue
// Admin dashboard analytics tab
class AdminViewModel : ViewModel() {

    private val _allIssues = MutableStateFlow<List<Issue>>(emptyList())
    val allIssues: StateFlow<List<Issue>> = _allIssues.asStateFlow()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val reportRepository = ReportRepositoryImpl(firestore)
    private val notificationRepository = NotificationRepositoryImpl(firestore)

    private val _stats = MutableStateFlow(AdminStats())
    val stats: StateFlow<AdminStats> = _stats.asStateFlow()
    
    private val _selectedFilter = MutableStateFlow<IssueStatus?>(null)
    val selectedFilter: StateFlow<IssueStatus?> = _selectedFilter.asStateFlow()

    private val _selectedCategory = MutableStateFlow<IssueCategory?>(null)
    val selectedCategory: StateFlow<IssueCategory?> = _selectedCategory.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadAllIssues()
    }
    
    fun loadAllIssues() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Load all issues from Firebase
                _allIssues.value = emptyList()
                reportRepository.getAllReports().collect { result ->
                    when(result) {
                        is DataResult.Success -> {
                            _allIssues.value = result.data
                        }
                        is DataResult.Error -> {
                            // Handle error
                        }
                        is DataResult.Loading -> {
                            // Handle loading
                        }
                        is DataResult.Idle -> {
                            // Handle idle
                        }
                    }
                }
                updateStats()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun updateStats() {
        val issues = _allIssues.value
        _stats.value = AdminStats(
            total = issues.size,
            pending = issues.count { it.status == IssueStatus.PENDING },
            active = issues.count { it.status == IssueStatus.IN_PROGRESS },
            resolved = issues.count { it.status == IssueStatus.RESOLVED }
        )
    }
    
    fun setFilter(status: IssueStatus?) {
        _selectedFilter.value = status
    }

    fun setCategoryFilter(category: IssueCategory?) {
        _selectedCategory.value = category
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun createNotification(title: String, message: String, issueId: String, reportedBy: String) {
        viewModelScope.launch {
            val newNotification = Notification(
                type = NotificationType.STATUS_UPDATE,
                title = title,
                message = message,
                issueId = issueId,
                timestamp = System.currentTimeMillis()
            )
            notificationRepository.createNotification(reportedBy, newNotification).collect { result->
                when(result) {
                    is DataResult.Success -> {
//                        _allIssues.value = _allIssues.value.map {
//                            if (it.id == issueId) it.copy(status = newStatus) else it
//                        }
                        updateStats()                            }
                    is DataResult.Error -> {
                        // Handle error
                    }
                    else -> {}
                }
            }
        }
    }

    // combined code for accepting/resolving status
    private fun updateIssueStatus(issueId: String, newStatus: IssueStatus, notificationTitle: String, notificationMessage: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val docRef = firestore.collection("reports").document(issueId)
                docRef.update(
                    mapOf(
                        "status" to newStatus.name,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()

                _allIssues.value = _allIssues.value.map {
                    if (it.id == issueId) it.copy(status = newStatus) else it
                }
                updateStats()

                val snapshot = docRef.get().await()
                val issue = snapshot.toObject(Issue::class.java)

                // once issue is updated, create a notification
                if (issue != null) {
                    val finalMessage = notificationMessage.format(issue.title)
                    createNotification(notificationTitle, finalMessage, issueId, issue.reportedBy)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun acceptIssue(issueId: String) {
        try {
            val acceptanceMessageTemplate = "Your issue \"%s\" has been reviewed and is now in progress."
            val acceptanceNotificationTitle = "Issue accepted for review"

            updateIssueStatus(issueId, IssueStatus.IN_PROGRESS, acceptanceNotificationTitle, acceptanceMessageTemplate)
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun resolveIssue(issueId: String) {
        try {
            val resolvedMessageTemplate = "Your issue \"%s\" has been resolved."
            val resolvedNotificationTitle = "Issue resolved"

            updateIssueStatus(issueId, IssueStatus.RESOLVED, resolvedNotificationTitle, resolvedMessageTemplate)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    fun getFilteredIssues(): List<Issue> {
        var filtered = _allIssues.value
        
        _selectedFilter.value?.let { status ->
            filtered = filtered.filter { it.status == status }
        }

        _selectedCategory.value?.let { category ->
            filtered = filtered.filter { it.category.equals(category.name, ignoreCase = true) }
        }
        
        if (_searchQuery.value.isNotBlank()) {
            filtered = filtered.filter { issue ->
                issue.title.contains(_searchQuery.value, ignoreCase = true) ||
                issue.description.contains(_searchQuery.value, ignoreCase = true) ||
                issue.reporterName.contains(_searchQuery.value, ignoreCase = true)
            }
        }
        
        return filtered
    }
}
