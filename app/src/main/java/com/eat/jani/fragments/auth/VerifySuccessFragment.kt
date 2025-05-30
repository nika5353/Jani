package com.eat.jani.fragments.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.eat.jani.R
import com.eat.jani.activities.auth.LoginActivity
import com.eat.jani.viewmodels.starting.SplashViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifySuccessFragment : Fragment() {
    private lateinit var v: View

    private lateinit var lottieView: LottieAnimationView
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_verify_success, container, false)

        lottieView = v.findViewById(R.id.successAnimation)
        viewModel.verificationSuccess()
        successAnimation()

        viewModel.successNavigate.observe(viewLifecycleOwner, Observer {
            if (it) {
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finish()
            }
        })

        return v
    }

    private fun successAnimation() {
        lifecycleScope.launch {
            delay(1100)
            lottieView.pauseAnimation()
        }
    }

}