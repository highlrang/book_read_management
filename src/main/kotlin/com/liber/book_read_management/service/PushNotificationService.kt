package com.liber.book_read_management.service

interface PushNotificationService {
    fun sendNotification(token: String, title: String, body: String)
}
