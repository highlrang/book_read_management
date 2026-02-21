package com.liber.book_read_management.repository.query

import com.liber.book_read_management.dto.BookReadLogResponse
import com.liber.book_read_management.dto.BookReadLogSearchRequest
import com.liber.book_read_management.dto.QBookReadLogResponse
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.entities.QBookReadLog.bookReadLog
import com.liber.book_read_management.enums.BookReadStatus
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.Expression
import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.Predicate
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.PathBuilder
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.function.LongSupplier

@Repository
class BookReadLogQueryRepositoryImpl(val jpaQueryFactory: JPAQueryFactory) : BookReadLogQueryRepository {
    override fun findByUserIdAndSearchParam(
        userId: Long,
        readLogSearchRequest: BookReadLogSearchRequest,
        pageable: Pageable
    ): Page<BookReadLogResponse> {

        val orders = getOrderSpecifiers(pageable.sort, BookReadLog::class.java, "bookReadLog")

        val content = jpaQueryFactory.select(
            QBookReadLogResponse(
                bookReadLog.id,
                bookReadLog.bookIsbn,
                bookReadLog.bookTitle,
                bookReadLog.bookAuthor,
                bookReadLog.bookThumbnailImage,
                bookReadLog.totalPage,
                bookReadLog.readStatus,
                bookReadLog.progressPercentage,
                bookReadLog.createdAt,
                bookReadLog.updatedAt
            )
        )
            .from(bookReadLog)
            .where(
                bookReadLog.userId.eq(userId),
                eqReadStatus(readLogSearchRequest.readStatus),
                betweenDate(readLogSearchRequest.startDate, readLogSearchRequest.endDate)
            )
            .orderBy(*orders)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = jpaQueryFactory.select(bookReadLog.id.count())
            .from(bookReadLog)
            .where(
                bookReadLog.userId.eq(userId),
                eqReadStatus(readLogSearchRequest.readStatus),
                betweenDate(readLogSearchRequest.startDate, readLogSearchRequest.endDate)
            )

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun eqReadStatus(readStatus: BookReadStatus?) : BooleanExpression? {
        if (readStatus == null || readStatus == BookReadStatus.ALL) return null

        return bookReadLog.readStatus.eq(readStatus)
    }

    fun betweenDate(startDate: LocalDate?, endDate: LocalDate?) : Predicate? {
        if (startDate == null && endDate == null)
            return null

        val builder = BooleanBuilder()
        startDate?.let{
            builder.and(bookReadLog.createdAt.goe(startDate.atStartOfDay()))
        }

        endDate?.let {
            builder.and(bookReadLog.createdAt.loe(endDate.atTime(23, 59, 59)))
        }

        return builder.value
    }

    private fun getOrderSpecifiers(sort: Sort, entityClass: Class<*>, entityName: String): Array<OrderSpecifier<*>> {
        val pathBuilder = PathBuilder(entityClass, entityName)

        return sort.map { order ->
            val direction = if (order.isAscending) Order.ASC else Order.DESC
            val prop = order.property
            OrderSpecifier(direction, pathBuilder.get(prop) as Expression<out Comparable<*>>)
        }.toList().toTypedArray()
    }
}