package com.eat.jani.fragments.main

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.eat.jani.R
import com.eat.jani.adapters.main.HomeCardAdapter
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentHomeBinding
import com.eat.jani.repositories.health.HealthRepository
import com.eat.jani.viewmodels.MealPlanningViewModelFactory
import com.eat.jani.viewmodels.main.HomeViewModel
import com.eat.jani.viewmodels.main.MealPlanningViewModel
import com.google.android.material.navigation.NavigationView

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: HomeCardAdapter
    private val mealPlanningViewModel: MealPlanningViewModel by viewModels {
        MealPlanningViewModelFactory(HealthRepository(RetrofitInstance.healthApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        mealPlanningViewModel.getInfo()
        observeCards()
        setUpListeners()

        return binding.root
    }

    private fun observeCards() {
        viewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            adapter = HomeCardAdapter(cards) { card ->
                when(card.title) {
                    "BMI და კალორიების ტრეკერი" -> navigateTo(R.id.nav_bmi_graph, R.id.nav_bmi)
                    "დიეტის დაგეგმვა" -> navigateTo(R.id.action_homeFragment_to_nav_meal_plan_graph, R.id.nav_meal_plan)
                    "კვების დღიური" -> navigateTo(R.id.action_homeFragment_to_nav_note_graph, R.id.nav_diary)
                    "პროდუქტების სია" -> navigateTo(R.id.action_homeFragment_to_productsFragment, R.id.nav_products)
                }
            }
            binding.cardRecyclerView.adapter = adapter
        })
        /*mealPlanningViewModel.progressResponse.observe(viewLifecycleOwner) {
            if(!it.isNullOrEmpty()) {
                binding.navigateToProgressBtn.visibility = View.VISIBLE
            } else {
                binding.navigateToProgressBtn.visibility = View.GONE
            }
        }*/
    }

    private fun navigateTo(fragmentId: Int, menuItemId: Int? = null) {
        findNavController().navigate(fragmentId)

        menuItemId?.let {
            val navView = requireActivity().findViewById<NavigationView>(R.id.mainNavView)
            navView.setCheckedItem(it)
        }
    }

    private fun setUpListeners() {
        binding.navigateToProgressBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_progressFragment)
        }
    }
}