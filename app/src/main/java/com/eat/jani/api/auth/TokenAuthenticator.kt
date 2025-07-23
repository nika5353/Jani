package com.eat.jani.api.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.eat.jani.JaniApp
import com.eat.jani.activities.auth.LoginActivity
import com.eat.jani.models.auth.TokenRefreshRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator (
    private val authApi: AuthApiService,
    private val context: Context
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val prefs = JaniApp.authSharedPreferences
        val oldAccessToken = prefs.getString("accessToken", null)
        val oldRefreshToken = prefs.getString("refreshToken", null)

        if (oldAccessToken.isNullOrEmpty() || oldRefreshToken.isNullOrEmpty()) return null

        Log.d("TOKEN", "🔁 Trying to refresh tokens")
        Log.d("TOKEN", "Old access: $oldAccessToken")
        Log.d("TOKEN", "Old refresh: $oldRefreshToken")

        return try {
            val newUserInfo = runBlocking {
                val refreshResponse = authApi.generateNewTokens(
                    TokenRefreshRequest(oldAccessToken, oldRefreshToken)
                )
                if (refreshResponse.isSuccessful) {
                    val body = refreshResponse.body()!!
                    Log.d("TOKEN", "✅ New tokens received: ${body.token} / ${body.refreshToken}")
                    body
                } else {
                    Log.e("TOKEN", "❌ Token refresh failed: ${refreshResponse.errorBody()?.string()}")
                    null
                }
            }

            if (newUserInfo != null) {
                // Save new tokens
                prefs.edit().apply {
                    putString("accessToken", newUserInfo.token)
                    putString("refreshToken", newUserInfo.refreshToken)
                    apply()
                }

                // Retry original request with new token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newUserInfo.token}")
                    .build()
            } else {
                // If refresh failed, log user out
                logoutUser()
                null
            }

        } catch (e: Exception) {
            Log.e("AUTH", "⚠️ Exception during token refresh: ${e.localizedMessage}")
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var current = response
        while (current.priorResponse != null) {
            count++
            current = current.priorResponse!!
        }
        return count
    }

    private fun logoutUser() {
        Log.d("AUTH", "🚪 Logging out due to failed refresh token")

        // Clear shared preferences
        JaniApp.authSharedPreferences.edit().apply {
            remove("accessToken")
            remove("refreshToken")
            apply()
        }

        // Redirect to login
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}