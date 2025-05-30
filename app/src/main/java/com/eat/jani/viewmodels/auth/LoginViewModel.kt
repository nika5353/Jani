package com.eat.jani.viewmodels.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.JaniApp
import com.eat.jani.models.auth.EmailRequest
import com.eat.jani.models.auth.LoginRequest
import com.eat.jani.models.auth.PasswordResetRequest
import com.eat.jani.repositories.auth.AuthRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    // Inputs
    val identifier = MutableLiveData<String>("")
    val password = MutableLiveData<String>("")

    // Inputs For Password Reset
    val email = MutableLiveData<String>("")
    val token = MutableLiveData<String>("")
    val newPassword = MutableLiveData<String>("")
    val confirmPassword = MutableLiveData<String>("")

    // Error Messages
    val emailError = MutableLiveData<String?>()
    val passwordError = MutableLiveData<String?>()
    val confirmPasswordError = MutableLiveData<String?>()
    val generalError = MutableLiveData<String?>()

    // States
    val loginSuccess = MutableLiveData<Boolean>()
    val emailSendSuccess = MutableLiveData<Boolean>()
    val passwordResetSuccess = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    //Token
    private var accessToken = ""
    private var refreshToken = ""

    // Validation
    private fun validateInputs(): Boolean {
        var isValid = true

        emailError.value = null
        passwordError.value = null

        if (identifier.value.isNullOrBlank()) {
            emailError.value = "შეიყვანეთ email-ი ან username"
            isValid = false
        }

        if (password.value.isNullOrBlank()) {
            passwordError.value = "შეიყვანეთ პაროლი"
            isValid = false
        }

        return isValid
    }

    fun loginUser() {
        if (!validateInputs()) return

        isLoading.value = true

        viewModelScope.launch {
            try {
                val request = LoginRequest(
                    identifier = identifier.value!!,
                    password = password.value!!
                )

                val response = repository.loginUser(request)

                if (response.isSuccessful) {
                    accessToken = response.body()?.information!!.token
                    refreshToken = response.body()?.information!!.refreshToken
                    if (accessToken.isNotBlank() && refreshToken.isNotBlank()) {
                        saveToken(accessToken, refreshToken)
                        loginSuccess.value = true
                    } else {
                        generalError.value = "არ გაგივლია ვერიფიკაცია"
                    }
                } else {
                    val detail = JSONObject(response.errorBody()?.string() ?: "").getString("detail")
                    if(detail.equals("Invalid username or email")) {
                        emailError.value = "არასწორი email ან username"
                    } else {
                        passwordError.value = "არასწორი პაროლი"
                    }
                    generalError.value = "ავტორიზაცია ვერ მოხდა"
                    Log.e("LOGIN_USER", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("LOGIN_USER", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun sendEmailForPassword() {
        if (email.value.isNullOrBlank()) {
            emailError.value = "შეიყვანეთ email-ი"
            return
        }

        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.sendEmailForPassword(EmailRequest(email.value!!))
                if (response.isSuccessful) {
                    emailSendSuccess.value = true
                } else {
                    generalError.value = "გაგზავნა ვერ მოხდა"
                    Log.e("SEND_EMAIL_LINK", "${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("SEND_EMAIL_LINK", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun resetPassword() {
        if (newPassword.value != confirmPassword.value) {
            passwordError.value = "პაროლები არ ემთხვევა"
            return
        }

        viewModelScope.launch {
            try {
                val request = PasswordResetRequest(
                    email = email.value!!,
                    passResetToken = token.value!!,
                    newPassword = newPassword.value!!,
                    confirmPassword = confirmPassword.value!!
                )
                val response = repository.resetPassword(request)

                if (response.isSuccessful) {
                    passwordResetSuccess.value = true
                } else {
                    generalError.value = "შეცვლა ვერ მოხდა"
                    Log.e("PASSWORD_RESET", "${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("PASSWORD_RESET", "${e.localizedMessage}")
            }
        }
    }

    private fun saveToken(accessToken: String, refreshToken: String) {
        val sharedPreferences = JaniApp.authSharedPreferences
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
        Log.d("TOKEN_SAVE", "Tokens Saved: $accessToken and $refreshToken")
    }
}