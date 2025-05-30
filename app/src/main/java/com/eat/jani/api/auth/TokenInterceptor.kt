package com.eat.jani.api.auth

import com.eat.jani.JaniApp
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = JaniApp.authSharedPreferences.getString("accessToken", null)
        val requestBuilder = chain.request().newBuilder()

        accessToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(requestBuilder.build())
    }
}