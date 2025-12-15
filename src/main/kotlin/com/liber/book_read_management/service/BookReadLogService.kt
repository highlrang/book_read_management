package com.liber.book_read_management.service

import com.liber.book_read_management.dto.BookRatingSaveRequest
import com.liber.book_read_management.dto.BookRatingUpdateRequest
import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import com.liber.book_read_management.dto.BookReviewSaveRequest

interface BookReadLogService {

    fun saveBookReadLog(userId: Long, readLogSaveRequest: BookReadLogSaveRequest) : Long
    suspend fun updatePage(userId: Long, readPageUpdateRequest: BookReadPageUpdateRequest)
    fun saveBookReview(userId: Long, reviewSaveRequest: BookReviewSaveRequest)
    fun saveBookRating(userId: Long, ratingSaveRequest: BookRatingSaveRequest)
    fun updateBookRating(userId: Long, ratingUpdateRequest: BookRatingUpdateRequest)

// TODO    fun getReadBooks(userId: Long) : List<BookInfo>
}