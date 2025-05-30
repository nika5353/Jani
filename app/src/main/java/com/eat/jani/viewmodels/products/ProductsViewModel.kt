package com.eat.jani.viewmodels.products

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.eat.jani.models.enums.ProductsCategory
import com.eat.jani.models.products.Product
import com.eat.jani.repositories.products.ProductsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class ProductsViewModel(private val repository: ProductsRepository) : ViewModel() {
    val searchInput = MutableLiveData<String>("")
    val categoryOption = MutableLiveData<ProductsCategory?>(null)

    // Results
    val products = MutableLiveData<List<Product>?>(emptyList())
    val success = MutableLiveData<Boolean>(false)
    val generalError = MutableLiveData<String?>(null)

    // Job
    private var searchJob: Job? = null

    val categoryDisplay = listOf(
        "ყველაფერი",
        "ცილები",
        "ნახშირწყლები",
        "ბოსტნეული",
        "ხილი"
    )
    val categoryMap = mapOf(
        "ყველაფერი" to null,
        "ცილები" to ProductsCategory.Protein,
        "ნახშირწყლები" to ProductsCategory.Carb,
        "ბოსტნეული" to ProductsCategory.Vegetable,
        "ხილი" to ProductsCategory.Fruit
    )

    init {
        viewModelScope.launch {
            searchInput.asFlow()
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest {
                    searchProducts()
                }
        }
    }

    fun setCategory(displayText: String) {
        categoryOption.value = categoryMap[displayText]
        if (!searchInput.value.isNullOrBlank()) {
            searchProducts()
        } else {
            success.value = false
            products.value = emptyList()
        }
    }

    fun searchProducts() {
        generalError.value = null

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                val searchRaw = searchInput.value?.trim() ?: ""
                val category = categoryOption.value?.name

                // თუ ძიება სავალდებულოა, ცარიელზე არ გავუშვათ
                if (searchRaw.isBlank()) {
                    success.value = false
                    products.value = emptyList()
                    return@launch
                }

                Log.d("PRODUCTS_REQUEST", "Search='$searchRaw', Category='${category ?: "ALL"}'")
                val response = repository.findProducts(searchRaw, category)

                if (response.isSuccessful) {
                    val productList = response.body() ?: emptyList()
                    products.value = productList
                    success.value = productList.isNotEmpty()
                } else {
                    products.value = emptyList()
                    success.value = false
                    Log.e("PRODUCTS", "${response.errorBody()?.string()}")
                }
            } catch (e: CancellationException) {
                // coroutine გაუქმდა — იგნორი OK
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("PRODUCTS_REQUEST", "Exception: ${e.localizedMessage}")
            }
        }
    }
}