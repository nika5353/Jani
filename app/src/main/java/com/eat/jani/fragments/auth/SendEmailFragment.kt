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
import com.eat.jani.databinding.FragmentSendEmailBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.LoginViewModelFactory
import com.eat.jani.viewmodels.auth.LoginViewModel

class SendEmailFragment : Fragment() {
    private lateinit var binding: FragmentSendEmailBinding
    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_send_email, container, false)
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

        viewModel.emailSendSuccess.observe(viewLifecycleOwner, Observer { success ->
            if(success) {
                Toast.makeText(requireContext(), "შეამოწმეთ თქვენი email", Toast.LENGTH_SHORT).show()
                viewModel.email.value = ""
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
        binding.returnLoginTextView.setOnClickListener {
            findNavController().navigate(R.id.action_sendEmailFragment_to_loginActivity2)
            requireActivity().finish()
        }
    }
}