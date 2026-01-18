package com.liber.book_read_management.dto.aladin

data class AladinBookDetailResponse(
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
    val item: List<Item>?
) {
    data class Item(
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
        val seriesInfo: SeriesInfo?,
        val subInfo: SubInfo?
    )
    data class SeriesInfo(
        val seriesId: Long?,
        val seriesLink: String?,
        val seriesName: String?
    )

    data class SubInfo(
        val ebookList: List<Any>?, // JSON에서는 [] 이므로 Any 로 처리
        val usedList: UsedList?,
        val subTitle: String?,
        val originalTitle: String?,
        val itemPage: Int?
    )
    data class UsedList(
        val aladinUsed: UsedDetail?,
        val userUsed: UsedDetail?,
        val spaceUsed: UsedDetail?
    )
    data class UsedDetail(
        val itemCount: Int?,
        val minPrice: Int?,
        val link: String?
    )
}


