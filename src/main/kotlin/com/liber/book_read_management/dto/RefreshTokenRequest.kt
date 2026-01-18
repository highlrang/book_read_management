package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "토큰 재발급 요청")
class RefreshTokenRequest(
    @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsImlhdCI6MTYyNzY0NjYyMiwiZXhwIjoxNjI4MjUxNDIyfQ.a_9e_e_q_A_S_D_F_G_H_J_K_L_Z_X_C_V_B_N_M")
    val refreshToken: String
)
