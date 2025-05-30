package com.eat.jani.viewmodels.starting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel : ViewModel() {
    private val _isLoadingCompleted = MutableLiveData<Boolean>()
    val isLoadingCompleted: LiveData<Boolean> get() = _isLoadingCompleted

    private val _successNavigate = MutableLiveData<Boolean>()
    val successNavigate: LiveData<Boolean> get() = _successNavigate

    fun loadResources() {
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)

            withContext(Dispatchers.Main) {
                _isLoadingCompleted.value = true
            }
        }
    }

    fun verificationSuccess() {
        viewModelScope.launch {
            delay(3000)

            _successNavigate.value = true
        }
    }
}