package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookReadStatus
import com.liber.book_read_management.enums.SortDirection
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import java.time.LocalDate

@Getter
@Setter
@NoArgsConstructor
class BookReadLogSearchRequest(
    val readStatus: BookReadStatus,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val sort: SortDirection? = SortDirection.DESC
)