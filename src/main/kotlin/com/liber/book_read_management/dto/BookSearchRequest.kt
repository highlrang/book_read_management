package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.CategoryGroup
import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 검색 요청")
class BookSearchRequest(
    @Schema(description = "검색어", example = "2026")
    val query: String?,
    @field:Schema(
        description = "검색 타입 (검색어 있을 때 적용됨)",
        example = "Keyword",
        defaultValue = "Keyword"
    )
    val queryType: BookQueryType = BookQueryType.Keyword,
    @field:Schema(
        description = "정렬 (검색어 있을 때 적용됨)",
        example = "Accuracy",
        defaultValue = "Accuracy"
    )
    val sort: BookSortType = BookSortType.Accuracy,
    @field:Schema(
        description = "카테고리 필터 (searchTarget=Book에서만 적용, eBook/Foreign 미적용)", // TODO 부정확함..
        example = "TECHNOLOGY"
    )
    val category: CategoryGroup? = null,
    @field:Schema(
        description = "검색 타겟",
        example = "Book",
        defaultValue = "Book"
    )
    val searchTarget: BookSearchTarget = BookSearchTarget.Book,
    @Schema(description = "페이지 번호", example = "1")
    val page: Int = 1,
    @Hidden
    @Schema(description = "페이지 사이즈", example = "20")
    val size: Int = 20,
) {
}

enum class BookQueryType(val value: String) {
    Keyword("Keyword"),
    Title("Title"),
    Author("Author"),
    Publisher("Publisher")
}

enum class BookSortType(val value: String) {
    Accuracy("Accuracy"),
    PublishTime("PublishTime"),
    Title("Title")
}

enum class BookSearchTarget(val value: String) {
    Book("Book"),
    Foreign("Foreign"),
    eBook("eBook")
}
