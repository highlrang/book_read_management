package com.liber.book_read_management.dto.aladin

data class AladinBookSearchResponse(
    val version: String?,
    val logo: String?,
    val title: String?,
    val link: String?,
    val pubDate: String?,
    val totalResults: Int?,
    val startIndex: Int?,
    val itemsPerPage: Int?,
    val query: String?,
    val searchCategoryId: Int?,
    val searchCategoryName: String?,
    val item: List<AladinItem>?
) {

    data class AladinItem(
        val title: String?,
        val link: String?,
        val author: String?,
        val pubDate: String?,
        val description: String?,
        val isbn: String?,
        val isbn13: String?,
        val itemId: Long?,
        val priceSales: Int?,
        val priceStandard: Int?,
        val mallType: String?,
        val stockStatus: String?,
        val mileage: Int?,
        val cover: String?,
        val categoryId: Int?,
        val categoryName: String?,
        val publisher: String?,
        val salesPoint: Int?,
        val adult: Boolean?,
        val fixedPrice: Boolean?,
        val customerReviewRank: Int?,
        val seriesInfo: AladinSeriesInfo?,
        val subInfo: AladinSubInfo?
    )

    data class AladinSeriesInfo(
        val seriesId: Long?,
        val seriesLink: String?,
        val seriesName: String?
    )

    data class AladinSubInfo(
        val ebookList: List<Any>?,
        val usedList: AladinUsedList?,
        val subTitle: String?,
        val originalTitle: String?,
        val itemPage: Int?
    )

    data class AladinUsedList(
        val aladinUsed: AladinUsedDetail?,
        val userUsed: AladinUsedDetail?,
        val spaceUsed: AladinUsedDetail?
    )

    data class AladinUsedDetail(
        val itemCount: Int?,
        val minPrice: Int?,
        val link: String?
    )
}