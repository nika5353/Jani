package com.eat.jani.api

import android.content.Context
import com.eat.jani.api.auth.AuthApiService
import com.eat.jani.api.auth.TokenAuthenticator
import com.eat.jani.api.auth.TokenInterceptor
import com.eat.jani.api.health.HealthApiService
import com.eat.jani.api.note.NoteApiService
import com.eat.jani.api.products.ProductsApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://10.0.2.2:7229/api/"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val baseClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .hostnameVerifier { hostname, _ ->
                hostname == "10.0.2.2" || hostname == "localhost"
            }
            .build()
    }

    private val baseApi: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(baseClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .hostnameVerifier { hostname, _ ->
                hostname == "10.0.2.2" || hostname == "localhost"
            }
            .addInterceptor(TokenInterceptor())
            .authenticator(TokenAuthenticator(baseApi, appContext))
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val healthApi: HealthApiService by lazy {
        retrofit.create(HealthApiService::class.java)
    }

    val productsApi: ProductsApiService by lazy {
        retrofit.create(ProductsApiService::class.java)
    }

    val noteApi: NoteApiService by lazy {
        retrofit.create(NoteApiService::class.java)
    }
}
