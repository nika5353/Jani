package com.eat.jani.activities.starting

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.eat.jani.MainActivity
import com.eat.jani.R
import com.eat.jani.viewmodels.starting.SplashViewModel

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()
    private lateinit var logo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        logo = findViewById(R.id.logo)
        val scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale)
        logo.startAnimation(scaleAnimation)

        viewModel.loadResources()
        // ვაკვირდებით, როდის ჩაიტვირთება ყველაფერი
        viewModel.isLoadingCompleted.observe(this, Observer { isComplete ->
            if (isComplete) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        })
    }
}