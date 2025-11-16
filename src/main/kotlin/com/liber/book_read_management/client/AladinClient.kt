package com.liber.book_read_management.client

import com.liber.book_read_management.dto.aladin.AladinBookDetailResponse
import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "aladin-client", url = "http://www.aladin.co.kr", configuration = [AladinClientConfig::class])
interface AladinClient {

    @GetMapping("/ttb/api/ItemSearch.aspx")
    fun searchItem(
        @RequestParam("Query") query: String,
        @RequestParam("QueryType") queryType: String = "Title",
        @RequestParam("MaxResults") maxResult: Int,
        @RequestParam("start") start: Int,
        @RequestParam("SearchTarget") searchTarget: String = "Book",
        @RequestParam("Version") version: String = "20131101",
        @RequestParam("Output") output: String = "JS"
    ) : AladinBookSearchResponse

    @GetMapping("/ttb/api/ItemLookUp.aspx")
    fun getItem(
        @RequestParam("itemIdType") itemIdType: String = "ISBN",
        @RequestParam("ItemId") itemItem: String,
        @RequestParam("Version") version: String = "20131101",
        @RequestParam("Output") output: String = "JS",
        @RequestParam("OptResult") optResult: String = "ebookList,usedList,reviewList"
    ) : AladinBookDetailResponse
}