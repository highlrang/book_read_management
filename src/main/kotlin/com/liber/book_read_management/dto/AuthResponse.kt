package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "인증 응답")
class AuthResponse (
    @Schema(description = "사용자 ID", example = "1")
    var userId: Long,
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYyNzY0NjYyMiwiZXhwIjoxNjI3NzMyMDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
    var accessToken: String,
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYyNzY0NjYyMiwiZXhwIjoxNjI4MjUxNDIyfQ.a_9e_e_q_A_S_D_F_G_H_J_K_L_Z_X_C_V_B_N_M")
    var refreshToken: String
)