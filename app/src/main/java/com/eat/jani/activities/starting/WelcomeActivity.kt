package com.eat.jani.activities.starting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.eat.jani.MainActivity
import com.eat.jani.R
import com.eat.jani.adapters.starting.WelcomePagerAdapter
import com.eat.jani.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var adapter: WelcomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)

        adapter = WelcomePagerAdapter(this)
        binding.viewPager2.adapter = adapter

        binding.nextButton.setOnClickListener {
            val nextItem = binding.viewPager2.currentItem + 1
            if (nextItem < adapter.itemCount) {
                binding.viewPager2.currentItem = nextItem
            } else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.skipButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        setupDots(adapter.itemCount)
        // როცა ViewPager ბოლო ელემენტზე მიდის, nextButton შეიცვლება Finish ღილაკზე
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                updateDots(position)

                if (position == adapter.itemCount - 1) {
                    binding.nextButton.text = "დაწყება"
                } else {
                    binding.nextButton.text = "შემდეგი"
                }
            }
        })
    }

    private fun setupDots(count: Int) {
        val dots = Array(count) { ImageView(this) }
        val layout = binding.dotsContainer
        layout.removeAllViews()

        for (i in dots.indices) {
            dots[i] = ImageView(this).apply {
                setImageResource(R.drawable.dot_inactive)
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16, 32, 16, 0)
                }
                layoutParams = params
            }
            layout.addView(dots[i])
        }

        // პირველი აქტიურია
        if (dots.isNotEmpty()) {
            dots[0].setImageResource(R.drawable.dot_active)
        }
    }

    private fun updateDots(position: Int) {
        val layout = binding.dotsContainer
        val count = layout.childCount
        for (i in 0 until count) {
            val imageView = layout.getChildAt(i) as ImageView
            imageView.setImageResource(if (i == position) R.drawable.dot_active else R.drawable.dot_inactive)
        }
    }
}