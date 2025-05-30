package com.eat.jani.repositories.auth

import com.eat.jani.api.auth.AuthApiService
import com.eat.jani.models.auth.EmailRequest
import com.eat.jani.models.auth.IsAvailableResponce
import com.eat.jani.models.auth.LoginRequest
import com.eat.jani.models.auth.LoginResponce
import com.eat.jani.models.auth.MessageResponce
import com.eat.jani.models.auth.PasswordResetRequest
import com.eat.jani.models.auth.RegisterRequest
import com.eat.jani.models.auth.UserResponce
import com.eat.jani.models.auth.UsernameRequest
import com.eat.jani.models.auth.VerifyRequest
import retrofit2.Response

class AuthRepository(private val api: AuthApiService) {
    suspend fun registerUser(request: RegisterRequest): Response<MessageResponce> {
        return api.register(request)
    }

    suspend fun sendVerificationCode(request: EmailRequest): Response<MessageResponce> {
        return api.sendVerificationCode(request)
    }

    suspend fun verifyEmail(request: VerifyRequest): Response<Unit> {
        return api.verify(request)
    }

    suspend fun loginUser(request: LoginRequest): Response<LoginResponce> {
        return api.login(request)
    }

    suspend fun sendEmailForPassword(request: EmailRequest): Response<MessageResponce> {
        return api.sendEmailForPassword(request)
    }

    suspend fun resetPassword(request: PasswordResetRequest): Response<MessageResponce> {
        return api.resetPassword(request)
    }

    suspend fun isEmailRegistered(request: EmailRequest): Response<IsAvailableResponce> {
        return api.isEmailRegistered(request)
    }

    suspend fun isUsernameRegistered(request: UsernameRequest): Response<IsAvailableResponce> {
        return api.isUsernameRegistered(request)
    }

    suspend fun logoutUser(): Response<Unit> {
        return api.logout()
    }

    suspend fun getAuthorizedUser() : Response<UserResponce> {
        return api.getUser()
    }
}