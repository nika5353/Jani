package com.eat.jani.viewmodels.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eat.jani.models.auth.EmailRequest
import com.eat.jani.models.auth.RegisterRequest
import com.eat.jani.models.auth.UsernameRequest
import com.eat.jani.models.auth.VerifyRequest
import com.eat.jani.repositories.auth.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject


class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {
    // Input values
    val username = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")
    val verificationCode = MutableLiveData("")

    // Error messages
    val usernameError = MutableLiveData<String?>(null)
    val emailError = MutableLiveData<String?>(null)
    val passwordError = MutableLiveData<String?>(null)
    val confirmPasswordError = MutableLiveData<String?>(null)
    val codeError = MutableLiveData<String?>(null)
    val generalError = MutableLiveData<String?>(null)

    // States
    val codeSentSuccess = MutableLiveData<Boolean>()
    val verificationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess = MutableLiveData<Boolean>()
    val usernameAvailability = MutableLiveData<Boolean>()
    val emailAvailability = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()

    // Validation
    private fun validateInputs(): Boolean {
        var isValid = true

        if (username.value.isNullOrBlank()) {
            usernameError.value = "შეიყვანეთ username"
            isValid = false
        }

        if (email.value.isNullOrBlank()) {
            emailError.value = "შეიყვანეთ email"
            isValid = false
        } else if (!isEmailValid(email.value!!)) {
            emailError.value = "ელფოსტა უნდა იყოს gmail.com, yahoo.com, outlook.com, hotmail.com ან mail.ru"
            isValid = false
        }

        if (password.value.isNullOrBlank()) {
            passwordError.value = "შეიყვანეთ პაროლი"
            isValid = false
        } else {
            val passwordValidationError = isPasswordValid(password.value!!)
            if (passwordValidationError != null) {
                passwordError.value = passwordValidationError
                isValid = false
            }
        }

        if (confirmPassword.value.isNullOrBlank() && !username.value.isNullOrBlank()
            && !email.value.isNullOrBlank() && !password.value.isNullOrBlank()) {
            confirmPasswordError.value = "გაიმეორე პაროლი"
            isValid = false
        } else if (password.value != confirmPassword.value) {
            confirmPasswordError.value = "პაროლები არ ემთხვევა"
            confirmPassword.value = ""
            isValid = false
        }

        return isValid
    }
    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@(?:gmail\\.com|yahoo\\.com|outlook\\.com|hotmail\\.com|mail\\.ru)$")
        return emailRegex.matches(email)
    }
    private fun isPasswordValid(password: String): String? {
        if (password.length < 8) {
            return "პაროლი უნდა შეიცავდეს მინიმუმ 8 ასოს"
        } else if (!password.any { it.isUpperCase() }) {
            return "პაროლი უნდა შეიცავდეს მინიმუმ ერთ დიდ ასოს"
        } else if (!password.any { it.isDigit() }) {
            return "პაროლი უნდა შეიცავდეს მინიმუმ ერთ ციფრს"
        } else if (!password.any { "!@#\$%^&*()_+\\-={}:;\"'<>?,./".contains(it) }) {
            return "პაროლი უნდა შეიცავდეს მინიმუმ ერთ სიმბოლოს (@#$%^&*)"
        }

        return null
    }

    fun checkEmailAvailability() {
        emailAvailability.value = true
        viewModelScope.launch {
            try {
                val emailRes = repository.isEmailRegistered(EmailRequest(email.value ?: ""))
                if (emailRes.body()?.isAvailable == false) {
                    emailAvailability.value = false
                    emailError.value = "Email უკვე გამოყენებულია"
                } else {
                    emailError.value = ""
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("VALIDATION", "Exception: ${e.localizedMessage}")
            }
        }
    }
    fun checkUsernameAvailability() {
        usernameAvailability.value = true
        viewModelScope.launch {
            try {
                val usernameRes = repository.isUsernameRegistered(UsernameRequest(username.value ?: ""))
                if (usernameRes.body()?.isAvailable == false) {
                    usernameAvailability.value = false
                    usernameError.value = "Username უკვე გამოყენებულია"
                } else {
                    usernameError.value = ""
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("VALIDATION", "Exception: ${e.localizedMessage}")
            }
        }
    }

/*    fun checkUsernameAndEmailAvailability(onAvailable: () -> Unit) {
        viewModelScope.launch {
            try {
                val usernameRes = repository.isUsernameRegistered(UsernameRequest(username.value ?: ""))
                val emailRes = repository.isEmailRegistered(EmailRequest(email.value ?: ""))

                var available = true

                if (usernameRes.body()?.isAvailable == false) {
                    usernameError.value = "Username უკვე გამოყენებულია"
                    available = false
                }
                if (emailRes.body()?.isAvailable == false) {
                    emailError.value = "Email უკვე გამოყენებულია"
                    available = false
                }

                if (available) {
                    onAvailable()
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("VALIDATION", "Exception: ${e.localizedMessage}")
            }
        }
    }*/

    fun registerUser() {
        if (!validateInputs()) return

        isLoading.value = true
        viewModelScope.launch {
            try {
                val request = RegisterRequest(
                    username = username.value!!,
                    email = email.value!!,
                    password = password.value!!,
                    confirmPassword = confirmPassword.value!!
                )

                val response = repository.registerUser(request)

                if (response.isSuccessful) {
                    registrationSuccess.value = true
                } else {
                    /*val detail = JSONObject(response.errorBody()?.string() ?: "").getString("detail")
                    val title = JSONObject(response.errorBody()?.string() ?: "").getString("title")
                    if(detail.equals("Username '${username.value}' is already taken.")) {
                        usernameError.value = "Username უკვე გამოყენებულია"
                    }
                    if(title.equals("Email already in use")) {
                        emailError.value = "Email უკვე გამოყენებულია"
                    }*/
                    generalError.value = "რეგისტრაცია ვერ მოხდა"
                    Log.e("REGISTER_USER", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("REGISTER_USER", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun sendVerificationCode() {
        isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.sendVerificationCode(EmailRequest(email.value ?: ""))

                Log.d("SEND_CODE", "Response code: ${response.code()}, body: ${response.body()}, errorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    codeSentSuccess.value = true
                } else {
                    generalError.value = "კოდის გაგზავნა ვერ მოხდა"
                    Log.e("SEND_CODE", "Error Body: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("SEND_CODE", "${generalError.value}: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun verifyCodeAndLogin() {
        codeError.value = null
        isLoading.value = true

        if (verificationCode.value.isNullOrBlank()) {
            codeError.value = "შეიყვანე კოდი"
            return
        }

        viewModelScope.launch {
            try {
                val request = VerifyRequest(
                    email = email.value ?: "",
                    code = verificationCode.value ?: ""
                )
                Log.e("VERIFY", "Email: ${email.value}")
                val response = repository.verifyEmail(request)

                if (response.isSuccessful) {
                    verificationSuccess.value = true
                } else {
                    codeError.value = "კოდი არასწორია ან ვადაგასულია"
                    Log.e("VERIFY", "Error Body: ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("VERIFY", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }
}
