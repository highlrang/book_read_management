package com.liber.book_read_management.controller

import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.AppFeatureStatusResponse
import com.liber.book_read_management.service.AppFeatureService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "앱 기능 API", description = "클라이언트 기능 활성 상태 API")
@RestController
@RequestMapping("/api/v1/app/features")
class AppFeatureApiController(
    private val appFeatureService: AppFeatureService
) {
    @Operation(summary = "앱 기능 상태 조회", description = "클라이언트에서 기능별 활성 상태를 조회합니다.")
    @GetMapping("/status")
    fun getFeatureStatus(): ResponseEntity<ApiResponse<AppFeatureStatusResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(appFeatureService.getStatus())
        )
    }
}
