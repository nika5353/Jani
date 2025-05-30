package com.eat.jani.fragments.auth

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentVerifyBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.auth.RegisterViewModel
import com.eat.jani.viewmodels.RegisterViewModelFactory

class VerifyFragment : Fragment() {
    private lateinit var binding: FragmentVerifyBinding
    private val viewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    //For timer and getting email
    private var emailFromArgs: String? = null
    private var timer: CountDownTimer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_verify, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // get email
        emailFromArgs = arguments?.getString("email")
        viewModel.email.value = emailFromArgs

        setUpObservers()
        startDeleteUserTimer()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.generalError.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                Log.d("SEND_CODE", "Email: ${viewModel.email.value}")
            }
        })

        viewModel.codeSentSuccess.observe(viewLifecycleOwner, Observer { success ->
            if(success) {
                Toast.makeText(requireContext(), "კოდი გაიგზავნა თქვენს email - ზე", Toast.LENGTH_SHORT).show()
                binding.codeTimerTextLayout.visibility = View.VISIBLE
            }
        })

        viewModel.verificationSuccess.observe(viewLifecycleOwner, Observer { success ->
            if (success) {
                timer?.cancel()
                findNavController().navigate(
                    R.id.action_emailVerifyFragment_to_verifySuccessFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.emailVerifyFragment, true)
                        .build()
                )
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

    private fun startDeleteUserTimer() {
        timer = object : CountDownTimer(300_000, 1_000) {
            override fun onTick(p0: Long) {
                binding.codeTimerTextView.text = "${p0 / 1_000}"
                Log.d("TIMER", "Seconds: ${p0 / 1000}")
            }

            override fun onFinish() {
                Log.d("TIMER", "Timer ended")
                binding.codeTimerTextLayout.visibility = View.INVISIBLE
                timer?.cancel()
                findNavController().navigate(R.id.action_emailVerifyFragment_to_registerFragment)
                requireActivity().finish()
            }
        }
        timer?.start()
    }
}