package com.liber.book_read_management.service

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.PageResponse
import org.springframework.stereotype.Service

@Service
class BookServiceImpl(
    var aladinClient: AladinClient
) : BookService {
    override fun searchBook(bookSearchRequest: BookSearchRequest): PageResponse<BookSearchResponse> {
        val aladinBookSearchResponse = aladinClient.searchItem(
            query = bookSearchRequest.query,
            maxResult = bookSearchRequest.size,
            start = bookSearchRequest.page * bookSearchRequest.size + 1
        )
        return PageResponse()
    }

    override fun getBookDetail(isbn: String): BookDetailResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        return BookDetailResponse.of(aladinBookDetailResponse)
    }


}