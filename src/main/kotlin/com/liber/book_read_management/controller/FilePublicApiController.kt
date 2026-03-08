package com.liber.book_read_management.controller

import com.liber.book_read_management.service.FileService
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Hidden
@RestController
@RequestMapping("/app/uploads")
class FilePublicApiController(
    private val fileService: FileService
) {

    @GetMapping("/{fileName:.+}")
    fun getUploadedFile(@PathVariable fileName: String): ResponseEntity<Resource> {
        val (resource, contentType) = fileService.loadFileResourceByStoredFileName(fileName)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource)
    }
}
