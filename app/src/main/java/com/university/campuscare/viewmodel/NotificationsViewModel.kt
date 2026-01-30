package com.university.campuscare.viewmodel

import android.util.Log
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
// Delete ALL notifications from the UI
// Mark individual notifications as read from the UI
// Mark ALL notifications as read from the UI
class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val notificationRepository = NotificationRepositoryImpl(firestore)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Load list of notifications for user
    fun loadNotifications(userId: String? = null) {
        if (userId == null) return
        
        viewModelScope.launch {
            try {
                notificationRepository.getNotificationsByUser(userId).collect { result ->
                    when (result) {
                        is DataResult.Loading -> _isLoading.value = true
                        is DataResult.Success -> {
                            _notifications.value = result.data
                            _isLoading.value = false
                        }
                        is DataResult.Error -> {
                            _isLoading.value = false
                            _error.value = result.error.peekContent()
                            Log.e("NotificationsViewModel", "Error loading notifications: ${result.error.peekContent()}")
                        }
                        else -> _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationsViewModel", "Exception in loadNotifications: ${e.message}")
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    // Mark a notification as read - TODO in the UI
    fun markAsRead(userId: String, notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(userId, notificationId).collect { result ->
                    if (result is DataResult.Success) {
                        _notifications.value = _notifications.value.map {
                            if (it.id == notificationId) it.copy(isRead = true) else it
                        }
                    } else if (result is DataResult.Error) {
                        Log.e("NotificationsViewModel", "Error marking notification as read: ${result.error.peekContent()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("NotificationsViewModel", "Exception in markAsRead: ${e.message}")
            }
        }
    }

    // Mark all notifications as read - TODO in the UI
    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                // Note: If the repository doesn't have markAllAsRead, 
                // you might loop or add that function to the repository.
                // For now, updating local state for immediate feedback
                _notifications.value.filter { !it.isRead }.forEach { 
                    markAsRead(userId, it.id)
                }
            } catch (e: Exception) {
                Log.e("NotificationsViewModel", "Exception in markAllAsRead: ${e.message}")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
