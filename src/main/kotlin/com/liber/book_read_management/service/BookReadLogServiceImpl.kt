package com.liber.book_read_management.service

import com.liber.book_read_management.dto.*
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.entities.BookReadProgress
import com.liber.book_read_management.enums.BookPageType
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.BookRatingLogRepository
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReadProgressRepository
import com.liber.book_read_management.repository.BookReviewLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookReadLogServiceImpl(
    private var bookReadLogRepository: BookReadLogRepository,
    private var bookReadProgressRepository: BookReadProgressRepository,
    private val bookReviewLogRepository: BookReviewLogRepository,
    private val bookRatingLogRepository: BookRatingLogRepository
) : BookReadLogService {

    /**
     * 독서 기록 저장
     *   도서와 독서 로그는 1:1이기에 해당 도서에 독서 로그가 없을 경우에만 insert
     */
    @Transactional
    override fun saveBookReadLog(
        userId: Long,
        readLogSaveRequest: BookReadLogSaveRequest
    ) : Long {
        val existBookReadLog: BookReadLog? = bookReadLogRepository.findByUserIdAndBookIsbn(
            userId,
            readLogSaveRequest.bookSbn
        )
        if (existBookReadLog != null)
            throw ApiException(ExceptionType.ALREADY_EXIST)

        val bookReadLog = BookReadLog.of(userId, readLogSaveRequest)
        val savedBookReadLog = bookReadLogRepository.save(bookReadLog)
        bookReadProgressRepository.save(BookReadProgress.init(userId, savedBookReadLog.id!!))

        return savedBookReadLog.id!!
    }

    override fun searchBookReadLogs(
        userId: Long,
        readLogSearchRequest: BookReadLogSearchRequest
    ): List<BookReadLogResponse> {
        return bookReadLogRepository.findByUserIdAndSearchParam(userId, readLogSearchRequest)
    }

    @Transactional
    override fun updatePage(userId: Long, bookReadPageUpdateRequest: BookReadPageUpdateRequest) {
        val type = bookReadPageUpdateRequest.type

        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, bookReadPageUpdateRequest.bookReadLogId)!!

        if (type == BookPageType.TOTAL)
            bookReadLog.totalPage = bookReadPageUpdateRequest.page
        else {
            val bookReadProgress = bookReadProgressRepository.findByUserIdAndBookReadLogId(userId, bookReadPageUpdateRequest.bookReadLogId)!!
            bookReadProgress.updateReadPage(bookReadPageUpdateRequest.page, bookReadLog.totalPage)
        }

    }

    @Transactional
    override fun saveBookReview(
        userId: Long,
        reviewSaveRequest: BookReviewSaveRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, reviewSaveRequest.bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        bookReviewLogRepository.save(reviewSaveRequest.toEntity())
    }

    @Transactional
    override fun saveBookRating(
        userId: Long,
        ratingSaveRequest: BookRatingSaveRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, ratingSaveRequest.bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val bookRatingLog = bookRatingLogRepository.findByBookReadLogId( ratingSaveRequest.bookReadLogId)
        if (bookRatingLog != null)
            throw ApiException(ExceptionType.ALREADY_EXIST)

        bookRatingLogRepository.save(ratingSaveRequest.toEntity())

    }

    @Transactional
    override fun updateBookRating(
        userId: Long,
        ratingUpdateRequest: BookRatingUpdateRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, ratingUpdateRequest.bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val bookRatingLog = bookRatingLogRepository.findByBookReadLogId( ratingUpdateRequest.bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        bookRatingLog.update(ratingUpdateRequest.rating, ratingUpdateRequest.content)
    }

}