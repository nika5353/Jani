package com.eat.jani.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.JaniApp
import com.eat.jani.repositories.auth.AuthRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AuthRepository) : ViewModel() {
    // Data
    val username = MutableLiveData<String>()
    val email = MutableLiveData<String>()

    // Error
    val generalError = MutableLiveData<String>()

    // State
    val logoutSuccess = MutableLiveData<Boolean>()

    fun getUser() {
        viewModelScope.launch {
            try {
                val response = repository.getAuthorizedUser()
                if(response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    username.value = user.username
                    email.value = user.email
                    Log.d("GET_USER", "${username.value} ${email.value}")
                } else {
                    generalError.value = "მომხმარებლის მონაცემების მიღება ვერ მოხერხდა"
                    Log.e("GET_USER", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("GET_USER", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            try {
                val response = repository.logoutUser()
                if (response.isSuccessful) {
                    // SharedPreferences-დან ტოკენის წაშლა
                    with(JaniApp.authSharedPreferences.edit()) {
                        remove("accessToken")
                        remove("refreshToken")
                        apply()
                    }
                    logoutSuccess.value = true
                } else {
                    generalError.value = "გამოსვლა ვერ მოხდა"
                    Log.e("LOGOUT", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("LOGOUT", "Exception: ${e.localizedMessage}")
            }
        }
    }
}
