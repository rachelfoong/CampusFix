package com.university.campuscare.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.university.campuscare.data.model.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    fun loadNotifications(userId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // TODO: Load from Firebase
                _notifications.value = emptyList()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                // TODO: Update in Firebase
                _notifications.value = _notifications.value.map {
                    if (it.id == notificationId) it.copy(isRead = true) else it
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                // TODO: Update all in Firebase
                _notifications.value = _notifications.value.map { it.copy(isRead = true) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
