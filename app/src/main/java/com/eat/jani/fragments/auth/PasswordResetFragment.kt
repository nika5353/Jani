package com.eat.jani.fragments.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentPasswordResetBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.LoginViewModelFactory
import com.eat.jani.viewmodels.auth.LoginViewModel
import kotlin.getValue

class PasswordResetFragment : Fragment() {
    private lateinit var binding: FragmentPasswordResetBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    // Arguments
    private var email: String? = null
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password_reset, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        email = arguments?.getString("email")
        token = arguments?.getString("token")
        viewModel.email.value = email
        viewModel.token.value = token

        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.generalError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.passwordResetSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                Toast.makeText(requireContext(), "პაროლი შეიცვალა, ახლა შედით თქვენს account - ზე", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_passwordResetFragment_to_loginActivity2)
                requireActivity().finish()
            }
        })
    }
}