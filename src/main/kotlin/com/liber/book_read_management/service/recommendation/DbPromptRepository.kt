package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.PromptJpaRepository
import org.springframework.stereotype.Repository

@Repository
class DbPromptRepository(
    private val promptJpaRepository: PromptJpaRepository
) : PromptRepository {

    override fun getPrompt(promptType: PromptType): String {
        return promptJpaRepository.findByPromptType(promptType)?.content
            ?: throw ApiException(
                ExceptionType.DATA_NOT_FOUND,
                "Prompt not found: ${promptType.name}"
            )
    }
}
