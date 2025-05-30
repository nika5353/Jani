package com.eat.jani.viewmodels.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.eat.jani.R
import com.eat.jani.models.main.HomeCard

class HomeViewModel : ViewModel() {
    private val _cards = MutableLiveData<List<HomeCard>>()
    val cards: LiveData<List<HomeCard>> get() = _cards

    init {
        loadCards()
    }

    private fun loadCards() {
        _cards.value = listOf(
            HomeCard("BMI და კალორიების ტრეკერი", R.drawable.bmi_calc),
            HomeCard("დიეტის დაგეგმვა", R.drawable.meal_plan),
            HomeCard("კვების დღიური", R.drawable.ic_food_note),
            HomeCard("პროდუქტების სია", R.drawable.food_products)
        )
    }
}