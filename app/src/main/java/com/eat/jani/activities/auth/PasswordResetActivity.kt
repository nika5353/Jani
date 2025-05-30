package com.eat.jani.activities.auth

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.eat.jani.R
import com.eat.jani.activities.BaseActivity

class PasswordResetActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        val data: Uri? = intent?.data
        val email = data?.getQueryParameter("email")
        val token = data?.getQueryParameter("token")

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment2) as NavHostFragment

        navHostFragment.viewLifecycleOwnerLiveData.observe(this) { lifecycleOwner ->
            if (lifecycleOwner != null && email != null && token != null) {
                val bundle = Bundle().apply {
                    putString("email", email)
                    putString("token", token)
                }
                navHostFragment.navController.navigate(R.id.passwordResetFragment, bundle)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment2) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }
}