package com.liber.book_read_management.controller

import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.FileResponseDto
import com.liber.book_read_management.service.FileService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "파일 API", description = "파일 관련 API")
@RestController
@RequestMapping("/api/v1/files")
class FileApiController(
    private val fileService: FileService
) {

    @Operation(summary = "파일 업로드", description = "서버에 파일을 업로드하고 아이디를 반환합니다.")
    @PostMapping(
        value = ["/upload"],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun uploadFile(@RequestPart("file") file: MultipartFile): ResponseEntity<ApiResponse<FileResponseDto>> {
        val fileResponseDto = fileService.uploadFile(file)
        return ResponseEntity.ok(ApiResponse.success(fileResponseDto))
    }
}
