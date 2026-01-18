package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookReadStatus
import io.swagger.v3.oas.annotations.media.Schema
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import org.springframework.data.domain.Sort.Direction;

@Schema(description = "책 읽기 기록 검색 요청")
@Getter
@Setter
@NoArgsConstructor
class BookReadLogSearchRequest(
    @Schema(description = "읽기 상태", example = "ALL")
    val readStatus: BookReadStatus,
    @Schema(description = "검색 시작일", example = "2026-01-01")
    val startDate: LocalDate? = null,
    @Schema(description = "검색 종료일", example = "2026-01-31")
    val endDate: LocalDate? = null
)