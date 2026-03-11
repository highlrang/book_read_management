package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.dto.semantic.BookInfoRequest
import java.security.MessageDigest

object RecommendationCacheKeyGenerator {

    @JvmStatic
    fun generate(userId: Long, request: BookInfoRequest): String {
        val source = buildString {
            append(userId)
            append("::")
            append(request.title)
            append("::")
            append(request.introduction)
            append("::")
            append(request.reviews.joinToString("||"))
        }

        val digest = MessageDigest.getInstance("SHA-256").digest(source.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
