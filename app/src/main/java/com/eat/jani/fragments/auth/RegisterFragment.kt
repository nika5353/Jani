package com.eat.jani.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eat.jani.R
import com.eat.jani.activities.auth.LoginActivity
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentRegisterBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.auth.RegisterViewModel
import com.eat.jani.viewmodels.RegisterViewModelFactory

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.generalError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.registrationSuccess.observe(viewLifecycleOwner, Observer { success ->
            if(success) {
                val email = viewModel.email.value ?: ""
                val bundle = Bundle().apply {
                    putString("email", email)
                }
                findNavController().navigate(R.id.action_registerFragment_to_emailVerifyFragment, bundle)
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                binding.loadingOverlay.visibility = View.VISIBLE
                binding.loadingAnimation.visibility = View.VISIBLE
                binding.loadingAnimation.playAnimation()
            } else {
                binding.loadingOverlay.visibility = View.GONE
                binding.loadingAnimation.visibility = View.GONE
                binding.loadingAnimation.cancelAnimation()
            }
        })
    }

    private fun setUpListeners() {
        binding.loginTextView.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
        binding.usernameInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.checkUsernameAvailability()
            }
        }
        binding.emailInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.checkEmailAvailability()
            }
        }
    }
}
