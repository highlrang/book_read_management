package com.liber.book_read_management.service

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import org.springframework.stereotype.Service
import java.awt.print.Book

@Service
class BookServiceImpl(
    var aladinClient: AladinClient
) : BookService {

    override fun searchBook(bookSearchRequest: BookSearchRequest): PageResponse<List<BookSearchResponse>> {
        val aladinBookSearchResponse = aladinClient.searchItem(
            query = bookSearchRequest.query,
            maxResult = bookSearchRequest.size,
            start = bookSearchRequest.page * bookSearchRequest.size + 1
        )

        var bookSearchResponseList : List<BookSearchResponse> = listOf()
        if (!aladinBookSearchResponse.item.isNullOrEmpty()) {
            bookSearchResponseList = aladinBookSearchResponse.item
                .stream()
                .map { item -> BookSearchResponse.of(item) }
                .toList()
        }

        return PageResponse(
            bookSearchRequest.page,
            bookSearchRequest.size,
            PageResponse.calTotalPage(aladinBookSearchResponse.totalResults, bookSearchRequest.size),
            bookSearchResponseList
        )
    }

    override fun getBookDetail(isbn: String): BookDetailResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        return BookDetailResponse.of(aladinBookDetailResponse)
    }


}