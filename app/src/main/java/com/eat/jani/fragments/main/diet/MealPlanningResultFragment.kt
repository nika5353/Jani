package com.eat.jani.fragments.main.diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.eat.jani.R
import com.eat.jani.adapters.plan.WeekPlanAdapter
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentMealPlanningResultBinding
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.MealPlanningViewModelFactory
import com.eat.jani.viewmodels.main.MealPlanningViewModel

class MealPlanningResultFragment : Fragment() {
    private lateinit var binding: FragmentMealPlanningResultBinding
    private val viewModel: MealPlanningViewModel by navGraphViewModels(R.id.nav_meal_plan_graph) {
        MealPlanningViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_meal_planning_result, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.planResultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.dietPlanResponse.observe(viewLifecycleOwner) { result ->
            binding.planResultRecyclerView.adapter = WeekPlanAdapter(result ?: emptyList(), requireContext())
        }

        binding.closeBtn.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(findNavController().graph.startDestinationId, true)
                .build()
            findNavController().navigate(R.id.homeFragment, null, navOptions)
        }

        return binding.root
    }
}