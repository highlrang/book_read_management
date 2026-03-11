package com.liber.book_read_management.service.recommendation

interface PromptRepository {
    fun getPrompt(promptType: PromptType): String
}
