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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToInt

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
    ) : BookReadLogResponse {
        val existBookReadLog: BookReadLog? = bookReadLogRepository.findByUserIdAndBookIsbn(
            userId,
            readLogSaveRequest.bookSbn
        )
        if (existBookReadLog != null)
            throw ApiException(ExceptionType.ALREADY_EXIST)

        val bookReadLog = BookReadLog.of(userId, readLogSaveRequest)
        val savedBookReadLog = bookReadLogRepository.save(bookReadLog)

        return BookReadLogResponse.from(savedBookReadLog)
    }

    override fun searchBookReadLogs(
        userId: Long,
        request: BookReadLogSearchRequest
    ): PageResponse<List<BookReadLogResponse>> {
        val sortDirection = if (request.sortDirection == SortDirection.asc) {
            Sort.Direction.ASC
        } else {
            Sort.Direction.DESC
        }
        val sort = Sort.by(sortDirection, request.sort.name)

        val pageable = PageRequest.of(request.page - 1, request.size, sort)
        val page = bookReadLogRepository.findByUserIdAndSearchParam(userId, request, pageable)
        return PageResponse(request.page, request.size, page.totalPages, page.content)
    }

    override fun getBookReadLog(userId: Long, readLogId: Long) : BookReadLogResponse {
        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, readLogId)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val bookReadLogResponse = BookReadLogResponse.from(bookReadLog)

        val bookReadProgress = bookReadProgressRepository.findTopByUserIdAndBookReadLogIdOrderByIdDesc(userId, readLogId)
        val readPage = bookReadProgress?.readPage

        bookReadLogResponse.readPage = readPage
        bookReadLogResponse.progressPercentage = calculateProgressInt(bookReadLog.totalPage, readPage)

        return bookReadLogResponse
    }

    @Transactional
    override fun updatePage(userId: Long, bookReadLogId: Long, readPageUpdateRequest: BookReadPageUpdateRequest) {
        val type = readPageUpdateRequest.type

        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, bookReadLogId)!!

        if (type == BookPageType.TOTAL) {
            bookReadLog.totalPage = readPageUpdateRequest.page

        } else {
            val lastProgress =
                bookReadProgressRepository.findTopByUserIdAndBookReadLogIdOrderByIdDesc(userId, bookReadLogId)

            if ((lastProgress?.readPage ?: 0) >= readPageUpdateRequest.page) {
                throw ApiException(ExceptionType.VALIDATION_ERROR)
            }

            bookReadProgressRepository.save(
                BookReadProgress.of(userId, bookReadLogId, readPageUpdateRequest.page)
            )
        }

    }

    fun calculateProgressInt(totalPage: Int?, readPage: Int?): Int {
        val total = totalPage ?: 0
        val read = readPage ?: 0

        if (total <= 0) return 0

        return ((read.toDouble() / total.toDouble()) * 100.0).roundToInt().coerceAtMost(100)
    }

    @Transactional
    override fun saveBookReview(
        userId: Long,
        bookReadLogId: Long,
        reviewSaveRequest: BookReviewSaveRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        bookReviewLogRepository.save(reviewSaveRequest.toEntity(userId, bookReadLogId))
    }

    @Transactional
    override fun saveBookRating(
        userId: Long,
        bookReadLogId: Long,
        ratingSaveRequest: BookRatingSaveRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val bookRatingLog = bookRatingLogRepository.findByBookReadLogId(bookReadLogId)
        if (bookRatingLog != null)
            throw ApiException(ExceptionType.ALREADY_EXIST)

        bookRatingLogRepository.save(ratingSaveRequest.toEntity(bookReadLogId))

    }

    @Transactional
    override fun updateBookRating(
        userId: Long,
        bookReadLogId: Long,
        ratingUpdateRequest: BookRatingUpdateRequest
    ) {
        bookReadLogRepository.findByUserIdAndId(userId, bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val bookRatingLog = bookRatingLogRepository.findByBookReadLogId(bookReadLogId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        bookRatingLog.update(ratingUpdateRequest.rating, ratingUpdateRequest.content)
    }

    override fun getBookReviewLogs(userId: Long, bookReadLogId: Long): List<BookReviewLogResponse> {
        val reviews = bookReviewLogRepository.findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId, bookReadLogId)
        return reviews.stream()
            .map{ review -> BookReviewLogResponse(review.readPage, review.content, review.createdAt!!.toLocalDate()) }
            .toList()
    }

    override fun getReadPageHistory(userId: Long, bookReadLogId: Long): List<BookReadPageResponse> {
        val readProgressList = bookReadProgressRepository.findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId, bookReadLogId)

        val bookReadPageHistoryList = mutableListOf<BookReadPageResponse>()

        for ((index) in readProgressList.withIndex()) {
            val current = readProgressList.get(index)
            val prevPage = if (index == readProgressList.size - 1) 0
                           else readProgressList.get(index + 1).readPage

            val diffPage : Int = current.readPage - prevPage

            bookReadPageHistoryList.add(
                BookReadPageResponse(prevPage, current.readPage, diffPage, current.createdAt!!.toLocalDate())
            )
        }

        return bookReadPageHistoryList
    }

}