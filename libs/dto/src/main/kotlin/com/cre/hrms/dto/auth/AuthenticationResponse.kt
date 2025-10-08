package com.cre.hrms.dto.auth

data class AuthenticationResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val username: String,
    val email: String
)
