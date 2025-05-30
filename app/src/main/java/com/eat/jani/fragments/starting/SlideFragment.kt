package com.eat.jani.fragments.starting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.eat.jani.R
import com.eat.jani.databinding.FragmentSlideBinding
import com.eat.jani.models.starting.Slide

class SlideFragment : Fragment() {
    companion object {
        private const val ARG_SLIDE = "slide"

        fun newInstance(slide: Slide) = SlideFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_SLIDE, slide)
            }
        }
    }

    private lateinit var binding: FragmentSlideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_slide, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val slide = arguments?.getParcelable<Slide>(ARG_SLIDE)

        slide?.let {
            binding.slide = it
        } ?: run {
            // Error
        }
    }
}