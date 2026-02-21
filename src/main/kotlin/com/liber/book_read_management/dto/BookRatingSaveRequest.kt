package com.liber.book_read_management.dto

import com.liber.book_read_management.entities.BookRatingLog
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 평점 저장 요청")
class BookRatingSaveRequest(
    @Schema(description = "평점", example = "5")
    val rating: Int,
    @Schema(description = "내용", example = "재미있어요")
    val content: String
) {

    fun toEntity(bookReadLogId: Long) : BookRatingLog {
        return BookRatingLog(
            bookReadLogId = bookReadLogId,
            rating = rating,
            content = content
        )
    }
}