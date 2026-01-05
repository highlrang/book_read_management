package com.liber.book_read_management.repository.query

import com.liber.book_read_management.dto.BookReadLogResponse
import com.liber.book_read_management.dto.BookReadLogSearchRequest
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class BookReadLogQueryRepositoryImpl(val jpaQueryFactory: JPAQueryFactory) : BookReadLogQueryRepository {
    override fun findByUserIdAndSearchParam(
        userId: Long,
        readLogSearchRequest: BookReadLogSearchRequest
    ): List<BookReadLogResponse> {
//        jpaQueryFactory.select()
//            .from(book)
        return ArrayList()
    }
}