package com.eat.jani.api.auth


import com.eat.jani.models.auth.EmailRequest
import com.eat.jani.models.auth.IsAvailableResponce
import com.eat.jani.models.auth.LoginRequest
import com.eat.jani.models.auth.LoginResponce
import com.eat.jani.models.auth.MessageResponce
import com.eat.jani.models.auth.PasswordResetRequest
import com.eat.jani.models.auth.RegisterRequest
import com.eat.jani.models.auth.TokenRefreshRequest
import com.eat.jani.models.auth.UserInfo
import com.eat.jani.models.auth.UserResponce
import com.eat.jani.models.auth.UsernameRequest
import com.eat.jani.models.auth.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("Registration/registration")
    suspend fun register(@Body request: RegisterRequest): Response<MessageResponce>

    @POST("Verification/send-email-verification-code")
    suspend fun sendVerificationCode(@Body request: EmailRequest): Response<MessageResponce>

    @POST("Verification/verify-email")
    suspend fun verify(@Body request: VerifyRequest): Response<Unit>

    @POST("Authentication/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponce>

    @POST("Password/Send-email-verification-link-for-password")
    suspend fun sendEmailForPassword(@Body request: EmailRequest): Response<MessageResponce>

    @POST("Password/reset-password")
    suspend fun resetPassword(@Body request: PasswordResetRequest): Response<MessageResponce>

    @POST("Authentication/generate-new-access-token")
    suspend fun generateNewTokens(@Body request: TokenRefreshRequest): Response<UserInfo>

    @POST("Registration/email-checker")
    suspend fun isEmailRegistered(@Body request: EmailRequest): Response<IsAvailableResponce>

    @POST("Registration/username-checker")
    suspend fun isUsernameRegistered(@Body request: UsernameRequest): Response<IsAvailableResponce>

    @POST("Authentication/logout")
    suspend fun logout(): Response<Unit>

    @GET("Home")
    suspend fun getUser() : Response<UserResponce>
}