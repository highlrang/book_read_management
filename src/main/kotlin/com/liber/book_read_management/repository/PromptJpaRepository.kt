package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.Prompt
import com.liber.book_read_management.service.recommendation.PromptType
import org.springframework.data.jpa.repository.JpaRepository

interface PromptJpaRepository : JpaRepository<Prompt, Long> {
    fun findByPromptType(promptType: PromptType): Prompt?
}
