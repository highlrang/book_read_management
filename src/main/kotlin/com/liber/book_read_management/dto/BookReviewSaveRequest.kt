package com.liber.book_read_management.dto

import com.liber.book_read_management.entities.BookReviewLog
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "책 리뷰 저장 요청")
class BookReviewSaveRequest(
    @Schema(description = "책 읽기 기록 ID", example = "1")
    var bookReadLogId: Long,
    @Schema(description = "읽은 페이지", example = "100")
    var readPage: Int,
    @Schema(description = "내용", example = "흥미진진하네요")
    var content: String,
) {
    fun toEntity() : BookReviewLog {
        return BookReviewLog(
            bookReadLogId = bookReadLogId,
            readPage = readPage,
            content = content
        )
    }
}