package com.liber.book_read_management.service

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.dto.BookCategoryResponse
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.enums.CategoryGroup
import com.liber.book_read_management.dto.BookSearchTarget
import com.liber.book_read_management.util.AladinCategoryMapper
import com.liber.book_read_management.util.CategoryLabel
import org.springframework.stereotype.Service

@Service
class BookServiceImpl(
    var aladinClient: AladinClient
) : BookService {

    override fun searchBook(bookSearchRequest: BookSearchRequest): PageResponse<List<BookSearchResponse>> {
        // TODO Aladin CategoryId는 국내도서(Book) 기준 매핑이라 eBook/Foreign(foriegn) 검색에는 적용하지 않는다.
        val categoryId = if (bookSearchRequest.searchTarget == BookSearchTarget.Book) {
            AladinCategoryMapper.getCategoryIdOrNull(bookSearchRequest.category)
        } else {
            null
        }

        val aladinBookSearchResponse =
            if (bookSearchRequest.query == null)
                aladinClient.getItemList(
                    categoryId = categoryId,
                    searchTarget = bookSearchRequest.searchTarget.value,
                    maxResult = bookSearchRequest.size,
                    start = bookSearchRequest.page * bookSearchRequest.size + 1
                )
            else
                aladinClient.searchItem(
                    query = bookSearchRequest.query,
                    queryType = bookSearchRequest.queryType.value,
                    categoryId = categoryId,
                    searchTarget = bookSearchRequest.searchTarget.value,
                    maxResult = bookSearchRequest.size,
                    start = bookSearchRequest.page * bookSearchRequest.size + 1,
                    sort = bookSearchRequest.sort.value
                )

        val bookSearchResponseList = (aladinBookSearchResponse.item ?: emptyList())
            .map { item -> BookSearchResponse.of(item) }

        return PageResponse(
            bookSearchRequest.page,
            bookSearchRequest.size,
            PageResponse.calTotalPage(aladinBookSearchResponse.totalResults, bookSearchRequest.size),
            bookSearchResponseList
        )
    }

    override fun getBookCategories(): List<BookCategoryResponse> {
        return CategoryGroup.values()
            .filter { category -> category != CategoryGroup.UNKNOWN }
            .map { category ->
                BookCategoryResponse(
                    code = category.name,
                    label = CategoryLabel.toKorean(category)
                )
            }
    }

    override fun getBookDetail(isbn: String): BookDetailResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        return BookDetailResponse.of(aladinBookDetailResponse)
    }


}
