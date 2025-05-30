package com.eat.jani.repositories.products

import com.eat.jani.api.products.ProductsApiService
import com.eat.jani.models.products.Product
import retrofit2.Response

class ProductsRepository(private val api: ProductsApiService) {
    suspend fun findProducts(searchQuery: String, categoryQuery: String?) : Response<List<Product>?> {
        return api.searchProducts(searchQuery, categoryQuery)
    }
}