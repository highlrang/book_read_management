package com.liber.book_read_management.service

import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.entities.BookReadProgress
import com.liber.book_read_management.enums.PageType
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReadProgressRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BookReadLogServiceImpl(
    var bookReadLogRepository: BookReadLogRepository,
    var bookReadProgressRepository: BookReadProgressRepository
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
    override fun updatePage(userId: Long, bookReadPageUpdateRequest: BookReadPageUpdateRequest) {
        val type = bookReadPageUpdateRequest.type

        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, bookReadPageUpdateRequest.bookReadLogId)!!

        if (type == PageType.TOTAL)
            bookReadLog.totalPage = bookReadPageUpdateRequest.page
        else {
            val bookReadProgress = bookReadProgressRepository.findByUserIdAndBookReadLogId(userId, bookReadPageUpdateRequest.bookReadLogId)!!
            bookReadProgress.updateReadPage(bookReadPageUpdateRequest.page, bookReadLog.totalPage)
        }

    }

}