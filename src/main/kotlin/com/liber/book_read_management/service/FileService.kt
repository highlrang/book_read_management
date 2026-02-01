package com.liber.book_read_management.service

import com.liber.book_read_management.dto.FileResponseDto
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun uploadFile(file: MultipartFile): FileResponseDto
}
