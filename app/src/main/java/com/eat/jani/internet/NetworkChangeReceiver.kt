package com.eat.jani.internet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NetworkChangeReceiver(
    private val onNetworkChanged: (Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val isConnected = NetworkUtils.isInternetAvailable(it)
            onNetworkChanged(isConnected)
        }
    }
}