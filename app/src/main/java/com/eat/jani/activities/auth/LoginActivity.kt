package com.eat.jani.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.eat.jani.MainActivity
import com.eat.jani.R
import com.eat.jani.activities.BaseActivity
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.ActivityLoginBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.LoginViewModelFactory
import com.eat.jani.viewmodels.auth.LoginViewModel

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.restorePassTextView.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }

        binding.registerTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun setupObservers() {
        viewModel.generalError.observe(this) {
            it?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                binding.loadingOverlay.visibility = View.VISIBLE
                binding.loadingAnimation.visibility = View.VISIBLE
                binding.loadingAnimation.playAnimation()
            } else {
                binding.loadingOverlay.visibility = View.GONE
                binding.loadingAnimation.visibility = View.GONE
                binding.loadingAnimation.cancelAnimation()
            }
        }
    }

    @Deprecated("Deprecated in java.")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}