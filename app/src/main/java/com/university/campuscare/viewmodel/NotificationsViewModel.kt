package com.university.campuscare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.university.campuscare.data.model.Notification
import com.university.campuscare.data.repository.NotificationRepositoryImpl
import com.university.campuscare.utils.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// TODO FOR NOTIFICATIONS:
// Note - AlertsTab.kt is currently used for the notifications UI, not NotificationsScreen!
// Delete a notification from the UI
// Mark individual notifications as read from the UI
// Mark ALL notifications as read from the UI
class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val notificationRepository = NotificationRepositoryImpl(firestore)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadNotifications(userId: String? = null) {
        if (userId == null) return
        
        viewModelScope.launch {
            notificationRepository.getNotificationsByUser(userId).collect { result ->
                when (result) {
                    is DataResult.Loading -> _isLoading.value = true
                    is DataResult.Success -> {
                        _notifications.value = result.data
                        _isLoading.value = false
                    }
                    is DataResult.Error -> {
                        _isLoading.value = false
                        // Handle error (e.g., show snackbar via another StateFlow)
                    }
                    else -> _isLoading.value = false
                }
            }
        }
    }
    
    fun markAsRead(userId: String, notificationId: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(userId, notificationId).collect { result ->
                if (result is DataResult.Success) {
                    _notifications.value = _notifications.value.map {
                        if (it.id == notificationId) it.copy(isRead = true) else it
                    }
                }
            }
        }
    }
    
    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            // Note: If the repository doesn't have markAllAsRead, 
            // you might loop or add that function to the repository.
            // For now, updating local state for immediate feedback
            _notifications.value.filter { !it.isRead }.forEach { 
                markAsRead(userId, it.id)
            }
        }
    }
}
