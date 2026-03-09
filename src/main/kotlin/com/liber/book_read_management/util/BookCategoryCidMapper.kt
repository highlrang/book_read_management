package com.liber.book_read_management.util

import com.liber.book_read_management.enums.BookCategory
import com.liber.book_read_management.dto.BookSearchTarget

object BookCategoryCidMapper {
    private val cidMap: Map<BookSearchTarget, Map<BookCategory, Int>> = mapOf(
        BookSearchTarget.Book to mapOf(
            BookCategory.NOVEL to 1,
            BookCategory.BUSINESS to 170,
            BookCategory.SELF_DEVELOPMENT to 336,
            BookCategory.IT to 351
        ),
        BookSearchTarget.Foreign to mapOf(
            BookCategory.NOVEL to 90842,
            BookCategory.BUSINESS to 90835,
            BookCategory.SELF_DEVELOPMENT to 90832,
            BookCategory.IT to 90846
        ),
        BookSearchTarget.eBook to mapOf(
            BookCategory.NOVEL to 38396,
            BookCategory.BUSINESS to 38398,
            BookCategory.SELF_DEVELOPMENT to 56388,
            BookCategory.IT to 38408
        )
    )

    fun getCid(searchTarget: BookSearchTarget, category: BookCategory): Int? {
        return cidMap[searchTarget]?.get(category)
    }
}
