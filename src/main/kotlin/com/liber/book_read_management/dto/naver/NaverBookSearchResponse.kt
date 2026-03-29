package com.liber.book_read_management.dto.naver

data class NaverBookSearchResponse(
    val lastBuildDate: String? = null,
    val total: Int = 0,
    val start: Int = 1,
    val display: Int = 0,
    val items: List<NaverBookItem> = emptyList()
) {
    data class NaverBookItem(
        val title: String? = null,
        val link: String? = null,
        val image: String? = null,
        val author: String? = null,
        val discount: String? = null,
        val publisher: String? = null,
        val isbn: String? = null,
        val description: String? = null,
        val pubdate: String? = null
    )
}
