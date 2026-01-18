package com.liber.book_read_management.dto

import com.liber.book_read_management.entities.BookRatingLog
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "책 평점 저장 요청")
class BookRatingSaveRequest(
    @Schema(description = "책 읽기 기록 ID", example = "1")
    val bookReadLogId: Long,
    @Schema(description = "평점", example = "5")
    val rating: Int,
    @Schema(description = "내용", example = "재미있어요")
    val content: String
) {

    fun toEntity() : BookRatingLog {
        return BookRatingLog(
            bookReadLogId = bookReadLogId,
            rating = rating,
            content = content
        )
    }
}