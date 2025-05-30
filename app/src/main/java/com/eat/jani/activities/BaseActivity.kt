package com.eat.jani.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.eat.jani.R
import com.eat.jani.activities.starting.SplashActivity
import com.eat.jani.internet.NetworkChangeReceiver
import com.eat.jani.internet.NetworkUtils

abstract class BaseActivity : AppCompatActivity() {

    private lateinit var overlay: View
    private lateinit var lottie: LottieAnimationView
    private lateinit var message: TextView

    private var networkReceiver: NetworkChangeReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (this.javaClass != SplashActivity::class.java) {
            val overlayView = layoutInflater.inflate(R.layout.no_internet_overlay, null)
            overlay = overlayView
            lottie = overlayView.findViewById(R.id.noInternetLottie)
            message = overlayView.findViewById(R.id.noInternetText)

            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            params.gravity = Gravity.CENTER

            // დაგვიანებული დამატება UI ხის დახატვის შემდეგ
            val rootView = findViewById<ViewGroup>(android.R.id.content)
            rootView.post {
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                params.gravity = Gravity.CENTER
                if (overlayView.parent == null) {
                    rootView.addView(overlayView, params)
                }
                checkInternet()
                registerNetworkReceiver()
            }
        }
    }

    private fun checkInternet() {
        val isConnected = NetworkUtils.isInternetAvailable(this)
        showOverlay(!isConnected)
    }

    private fun showOverlay(show: Boolean) {
        Log.d("Network", "No internet detected - showing overlay: $show")

        if (::overlay.isInitialized) {
            Log.d("OverlayDebug", "Overlay width: ${overlay.width}, height: ${overlay.height}")
            Log.d("OverlayDebug", "Overlay visibility: ${overlay.visibility}")
        }

        if (show) {
            overlay.visibility = View.VISIBLE
            lottie.playAnimation()
        } else {
            overlay.visibility = View.GONE
            lottie.cancelAnimation()
        }
    }

    private fun registerNetworkReceiver() {
        if (networkReceiver == null) {
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            networkReceiver = NetworkChangeReceiver { isConnected ->
                runOnUiThread {
                    showOverlay(!isConnected)
                }
            }
            registerReceiver(networkReceiver, filter)
        }
    }

    private fun unregisterNetworkReceiver() {
        try {
            if (!isFinishing && !isDestroyed && networkReceiver != null) {
                unregisterReceiver(networkReceiver)
                networkReceiver = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterNetworkReceiver()
    }

    override fun onResume() {
        super.onResume()
        registerNetworkReceiver()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkReceiver()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this.javaClass != SplashActivity::class.java) {
            unregisterNetworkReceiver()
        }
    }
}
