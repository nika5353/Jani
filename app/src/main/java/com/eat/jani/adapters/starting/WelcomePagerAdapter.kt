package com.eat.jani.adapters.starting

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eat.jani.R
import com.eat.jani.fragments.starting.SlideFragment
import com.eat.jani.models.starting.Slide

class WelcomePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    private val slides = listOf(
        Slide("", R.drawable.welcome,"კეთილი იყოს თქვენი მობრძანება კვების მონიტორინგისა და მართვის აპლიკაციაში.", ),
        Slide("კალორიების კალკულატორი", R.drawable.calories_calculator,"თქვენი ჯანმრთელობის მონაცემების მიხედვით თქვენ გამოითვლით თუ რამდენი კალორია უნდა მიიღოთ",),
        Slide("კვების დაგეგმვა", R.drawable.meal_planning,"აპლიკაციის საშუალებით თქვენ შეგიძლიათ დაგეგმოთ და აკონტროლოთ თქვენი კვება",),
        Slide("კვების დღიური", R.drawable.food_notes,"შეგიძლიათ ჩვენი კვების დღიური გამოიყენოთ თქვენი პროგრესის ან რაიმე სხვა ინფორმაციის შესანახად",),
        Slide("დაიწყე!", R.drawable.muscles,"დარეგისტრირდი, დაიწყე და გაძლიერდი ჩვენთან ერთად.",)
    )

    override fun getItemCount(): Int = slides.size

    override fun createFragment(position: Int): Fragment = SlideFragment.newInstance(slides[position])
}