package com.eat.jani.activities.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.graphics.toColorInt
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.eat.jani.R
import com.eat.jani.activities.BaseActivity
import com.eat.jani.activities.auth.LoginActivity
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.ActivityProfileBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.HealthViewModelFactory
import com.eat.jani.viewmodels.MainViewModel
import com.eat.jani.viewmodels.MainViewModelFactory
import com.eat.jani.viewmodels.MealPlanningViewModelFactory
import com.eat.jani.viewmodels.main.HealthViewModel
import com.eat.jani.viewmodels.main.MealPlanningViewModel

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }
    private val mealPlanningViewModel: MealPlanningViewModel by viewModels {
        MealPlanningViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.viewModel = viewModel
        binding.mealPlanViewModel = mealPlanningViewModel
        binding.lifecycleOwner = this

        viewModel.getUser()
        mealPlanningViewModel.getInfo()
        setUpObservers()
        setUpListeners()
    }

    private fun setUpObservers() {
        viewModel.generalError.observe(this, Observer {
            it?.let { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        })

        mealPlanningViewModel.healthInfo.observe(this) { data ->
            data?.let {
                when (mealPlanningViewModel.healthInfo.value?.healthStatus) {
                    "Underweight" -> binding.healthStatusInfoText.text = "დაბალწონიანი"
                    "Normal" -> binding.healthStatusInfoText.text = "ნორმალური"
                    "Overweight" -> binding.healthStatusInfoText.text = "ჭარბწონიანი"
                    "Obesity" -> binding.healthStatusInfoText.text = "სიმსუქნე"
                }
                binding.alertInfoLayout.visibility = View.GONE
                binding.healthInfoLayout.visibility = View.VISIBLE
            }
        }

        viewModel.logoutSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setUpListeners() {
        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }
    }

    @Deprecated("Deprecated by java")
    override fun onBackPressed() {
        super.onBackPressed()
    }
}