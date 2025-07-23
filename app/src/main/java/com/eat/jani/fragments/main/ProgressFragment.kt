package com.eat.jani.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eat.jani.R
import com.eat.jani.adapters.plan.ProgressAdapter
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentProgressBinding
import com.eat.jani.models.calculations.ProgressResponse
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.HealthViewModelFactory
import com.eat.jani.viewmodels.MealPlanningViewModelFactory
import com.eat.jani.viewmodels.main.HealthViewModel
import com.eat.jani.viewmodels.main.MealPlanningViewModel

class ProgressFragment : Fragment() {
    private lateinit var binding: FragmentProgressBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_progress, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val progressList = arguments?.getParcelableArrayList<ProgressResponse>("plan")

        binding.progressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.progressRecyclerView.adapter = ProgressAdapter(progressList ?: emptyList())

        binding.closeBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}