package com.liber.book_read_management.service

import com.liber.book_read_management.dto.*

interface BookReadLogService {

    fun saveBookReadLog(userId: Long, readLogSaveRequest: BookReadLogSaveRequest) : Long
    fun searchBookReadLogs(userId: Long, readLogSearchRequest: BookReadLogSearchRequest) : List<BookReadLogResponse>

    // TODO 서비스 분리
    fun updatePage(userId: Long, readPageUpdateRequest: BookReadPageUpdateRequest)
    fun saveBookReview(userId: Long, reviewSaveRequest: BookReviewSaveRequest)
    fun saveBookRating(userId: Long, ratingSaveRequest: BookRatingSaveRequest)
    fun updateBookRating(userId: Long, ratingUpdateRequest: BookRatingUpdateRequest)
}