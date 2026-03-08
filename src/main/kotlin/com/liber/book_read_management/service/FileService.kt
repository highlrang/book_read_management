package com.liber.book_read_management.service

import com.liber.book_read_management.dto.FileResponseDto
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun uploadFile(file: MultipartFile): FileResponseDto
    fun getFilePath(fileId: Long?): String?
    fun getFileUrl(filePath: String?): String?
    fun loadFileResource(fileId: Long): Pair<Resource, String>
    fun loadFileResourceByPath(filePath: String): Pair<Resource, String>
    fun loadFileResourceByStoredFileName(fileName: String): Pair<Resource, String>
}
