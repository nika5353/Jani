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

        val accessToken = JaniApp.authSharedPreferences.getString("accessToken", null)
        val refreshToken = JaniApp.authSharedPreferences.getString("refreshToken", null)

        if (accessToken.isNullOrEmpty() || refreshToken.isNullOrEmpty()) return null

        Log.d("GENERATE_NEW_TOKENS", "access Token: $accessToken, refresh token: $refreshToken")

        return try {
            val newUserInfo = runBlocking {
                val refreshResponse = authApi.generateNewTokens(
                    TokenRefreshRequest(accessToken, refreshToken)
                )
                if (refreshResponse.isSuccessful) {
                    Log.e("GENERATE_NEW_TOKENS", "${refreshResponse.body()}")
                    refreshResponse.body()
                } else {
                    Log.e("GENERATE_NEW_TOKENS", "Refresh Tokens Error Body: ${refreshResponse.errorBody()?.string()}")
                    logoutUser()
                    null
                }
            }

            newUserInfo?.let {
                with(JaniApp.authSharedPreferences.edit()) {
                    putString("accessToken", it.token)
                    putString("refreshToken", it.refreshToken)
                    apply()
                }
                Log.d("GENERATE_NEW_TOKENS", "access Token: $accessToken, refresh token: $refreshToken")
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${it.token}")
                    .build()
            }

        } catch (e: Exception) {
            Log.e("GENERATE_NEW_TOKENS", "${e.localizedMessage}")
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var res = response
        while (res.priorResponse != null) {
            count++
            res = res.priorResponse!!
        }
        return count
    }

    private fun logoutUser() {
        Log.d("TOKEN_DEBUG", "Logging out user due to expired refreshToken")

        with(JaniApp.authSharedPreferences.edit()) {
            remove("accessToken")
            remove("refreshToken")
            apply()
        }

        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}