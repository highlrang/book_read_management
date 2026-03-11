package com.liber.book_read_management.entities

import com.liber.book_read_management.service.recommendation.PromptType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "PROMPT")
class Prompt(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "prompt_type", nullable = false, unique = true, length = 100)
    var promptType: PromptType,

    @Lob
    @Column(name = "content", nullable = false)
    var content: String
) : BaseTimeEntity()
