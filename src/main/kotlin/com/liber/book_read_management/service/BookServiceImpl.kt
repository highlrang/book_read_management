package com.liber.book_read_management.service

import com.liber.book_read_management.client.AladinClient
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookPageResponse
import com.liber.book_read_management.dto.BookResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.aladin.AladinBookDetailResponse
import org.springframework.stereotype.Service

@Service
class BookServiceImpl(
    var aladinClient: AladinClient
) : BookService {
    override fun searchBook(bookSearchRequest: BookSearchRequest): BookPageResponse {

    }

    override fun getBookDetail(isbn: String): BookDetailResponse {
        val aladinBookDetailResponse = aladinClient.getItem(itemItem = isbn)
        return BookDetailResponse.of(aladinBookDetailResponse)
    }


}