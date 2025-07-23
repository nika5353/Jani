package com.eat.jani.notification

import com.google.firebase.messaging.FirebaseMessaging

fun fetchFirebaseToken(onTokenReceived: (String?) -> Unit) {
    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            onTokenReceived(task.result)
        } else {
            onTokenReceived(null)
        }
    }
}