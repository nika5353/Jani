package com.eat.jani.models.auth

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
)

data class VerifyRequest(
    val email: String,
    @SerializedName("verificationCode")
    val code: String
)

data class LoginRequest(
    @SerializedName("userNameOrEmail")
    val identifier: String,
    val password: String
)

data class PasswordResetRequest(
    val email: String,
    val passResetToken: String,
    val newPassword: String,
    val confirmPassword: String
)

data class TokenRefreshRequest(
    @SerializedName("token")
    val accessToken: String,
    val refreshToken: String
)

data class EmailRequest(
    val email: String
)

data class UsernameRequest(
    val username: String
)