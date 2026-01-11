package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookReadStatus
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter
import org.springframework.data.domain.PageRequest
import java.time.LocalDate
import org.springframework.data.domain.Sort.Direction;

@Getter
@Setter
@NoArgsConstructor
class BookReadLogSearchRequest(
    val readStatus: BookReadStatus,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null
)