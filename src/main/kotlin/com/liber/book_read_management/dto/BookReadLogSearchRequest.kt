package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookReadStatus
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import org.springframework.data.domain.Sort.Direction;

@Schema(description = "도서 읽기 기록 검색 요청")
@Getter
@Setter
@NoArgsConstructor
class BookReadLogSearchRequest(
    @Schema(description = "읽기 상태", example = "ALL")
    val readStatus: BookReadStatus,
    @Schema(description = "검색 시작일", example = "2026-01-01")
    val startDate: LocalDate? = null,
    @Schema(description = "검색 종료일", example = "2026-12-31")
    val endDate: LocalDate? = null,
    @Schema(description = "페이지", example = "1")
    val page: Int,
    @Schema(description = "페이지 사이즈", example = "20")
    val size: Int,
    @Schema(description = "정렬", example = "createdAt")
    val sort: SortType,
    @Schema(description = "정렬 방향", example = "desc")
    val sortDirection: SortDirection
)

enum class SortType {
    createdAt
}

enum class SortDirection {
    asc, desc
}