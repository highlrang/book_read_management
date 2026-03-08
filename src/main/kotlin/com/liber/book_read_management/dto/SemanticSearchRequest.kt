package com.liber.book_read_management.dto

data class SemanticSearchRequest(
    val bookReadLogId: Long // TODO optional하게 해서 사용자만으로도 검색 가능하도록
)