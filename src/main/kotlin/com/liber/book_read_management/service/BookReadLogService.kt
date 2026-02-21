package com.liber.book_read_management.service

import com.liber.book_read_management.dto.*
import org.springframework.data.domain.PageRequest

interface BookReadLogService {

    fun saveBookReadLog(userId: Long, readLogSaveRequest: BookReadLogSaveRequest) : BookReadLogResponse
    fun searchBookReadLogs(userId: Long, request: BookReadLogSearchRequest) : PageResponse<List<BookReadLogResponse>>
    fun getBookReadLog(userId: Long, readLogId: Long) : BookReadLogResponse
    fun updatePage(userId: Long, readPageUpdateRequest: BookReadPageUpdateRequest)
    fun saveBookReview(userId: Long, reviewSaveRequest: BookReviewSaveRequest)
    fun saveBookRating(userId: Long, ratingSaveRequest: BookRatingSaveRequest)
    fun updateBookRating(userId: Long, ratingUpdateRequest: BookRatingUpdateRequest)
}