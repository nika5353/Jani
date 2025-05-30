package com.eat.jani.api.products

import com.eat.jani.models.products.Product
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductsApiService {
    @GET("Products/search")
    suspend fun searchProducts(@Query("SearchTerm") searchQuery: String, @Query("CategoryOptions") categoryQuery: String? = null) : Response<List<Product>?>
}