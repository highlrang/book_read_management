package com.liber.book_read_management.service

import com.liber.book_read_management.dto.BookRatingSaveRequest
import com.liber.book_read_management.dto.BookRatingUpdateRequest
import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import com.liber.book_read_management.dto.BookReviewSaveRequest
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.entities.BookReadProgress
import com.liber.book_read_management.enums.PageType
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.BookRatingLogRepository
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReadProgressRepository
import com.liber.book_read_management.repository.BookReviewLogRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
//import kotlin.coroutines.coroutineScope

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
        val existBookReadLog : BookReadLog? = bookReadLogRepository.findByUserIdAndBookIsbn(
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

    @Transactional
    override suspend fun updatePage(userId: Long, bookReadPageUpdateRequest: BookReadPageUpdateRequest): Unit = coroutineScope {
        val type = bookReadPageUpdateRequest.type

        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, bookReadPageUpdateRequest.bookReadLogId)!!

        if (type == PageType.TOTAL)
            bookReadLog.totalPage = bookReadPageUpdateRequest.page
        else {
            val bookReadProgress = bookReadProgressRepository.findByUserIdAndBookReadLogId(userId, bookReadPageUpdateRequest.bookReadLogId)!!
            bookReadProgress.updateReadPage(bookReadPageUpdateRequest.page, bookReadLog.totalPage)
        }

        // TODO 3개월 지난 사용자는 제거하는 스케줄러
        // TODO Redis Data 없을 경우, 다시 넣는 조회 로직 만들기

        // 랭킹 업데이트
        launch {
            val completedCnt = bookReadProgressRepository.countByUserIdAndProgressAndUpdatedAtGreaterThanEqual(userId, 100, LocalDateTime.now().minusMonths(3))

            // 같은 연령대에서 비교
//            redisRankingStore.update(userId, )
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