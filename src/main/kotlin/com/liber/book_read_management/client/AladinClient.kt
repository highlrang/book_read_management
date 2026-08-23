package com.liber.book_read_management.client

import com.liber.book_read_management.dto.aladin.AladinBookDetailResponse
import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse
import jakarta.validation.constraints.Max
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "aladin-client", url = "https://www.aladin.co.kr", configuration = [AladinClientConfig::class])
interface AladinClient {

    @GetMapping("/ttb/api/ItemSearch.aspx")
    fun searchItem(
        @RequestParam("Query") query: String?,
        @RequestParam("QueryType") queryType: String = "Keyword",
        @RequestParam("CategoryId", required = false) categoryId: Int? = null,
        @RequestParam("Start") start: Int,
        @Max(100) @RequestParam("MaxResults") maxResult: Int,
        @RequestParam("SearchTarget") searchTarget: String = "Book",
        @RequestParam("Sort") sort: String = "Accuracy",
        @RequestParam("Version") version: String = "20131101",
        @RequestParam("Output") output: String = "JS"
    ) : AladinBookSearchResponse

    @GetMapping("/ttb/api/ItemList.aspx")
    fun getItemList(
        @RequestParam("QueryType") queryType: String = "BestSeller", // 필요하면 ItemNewSpecial 등 다른 목록 타입으로 교체 가능
        @RequestParam("CategoryId", required = false) categoryId: Int? = null,
        @RequestParam("SearchTarget") searchTarget: String = "Book",
        @RequestParam("Start") start: Int,
        @Max(100) @RequestParam("MaxResults") maxResult: Int,
        @RequestParam("Version") version: String = "20131101",
        @RequestParam("Output") output: String = "JS"
    ) : AladinBookSearchResponse

    @GetMapping("/ttb/api/ItemLookUp.aspx")
    fun getItem(
        @RequestParam("itemIdType") itemIdType: String = "ISBN",
        @RequestParam("ItemId") itemItem: String,
        @RequestParam("Version") version: String = "20131101",
        @RequestParam("Output") output: String = "JS"
    ) : AladinBookDetailResponse
}
