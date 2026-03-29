package com.liber.book_read_management.client

import com.liber.book_read_management.dto.naver.NaverBookSearchResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "naver-search-client",
    url = "https://openapi.naver.com",
    configuration = [NaverSearchClientConfig::class]
)
interface NaverSearchClient {

    @GetMapping("/v1/search/book.json")
    fun searchBook(
        @RequestParam("query") query: String,
        @RequestParam("display") display: Int = 10,
        @RequestParam("start") start: Int = 1,
        @RequestParam("sort") sort: String = "sim"
    ): NaverBookSearchResponse
}
