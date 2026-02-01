package com.liber.book_read_management.service

interface EmailService {
    fun sendVerificationEmail(email: String)
}