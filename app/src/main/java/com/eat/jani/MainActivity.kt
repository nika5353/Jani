package com.eat.jani

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.eat.jani.activities.BaseActivity
import com.eat.jani.activities.auth.LoginActivity
import com.eat.jani.activities.profile.ProfileActivity
import com.eat.jani.activities.starting.WelcomeActivity
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.ActivityMainBinding
import com.eat.jani.repositories.auth.AuthRepository
import com.eat.jani.viewmodels.MainViewModel
import com.eat.jani.viewmodels.MainViewModelFactory
import kotlin.getValue
import androidx.core.view.size
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.material.navigation.NavigationView

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(AuthRepository(RetrofitInstance.authApi))
    }

    private val fragmentToMenuMap = mapOf(
        R.id.homeFragment to R.id.nav_home,
        R.id.bmiFragment to R.id.nav_bmi,
        R.id.noteFragment to R.id.nav_diary,
        R.id.productsFragment to R.id.nav_products,
        R.id.mealPlanningFragment to R.id.nav_meal_plan
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        startingLogics()
        setUpListeners()
        fragmentCheck()

    }

    private fun startingLogics() {
        val isFirstRun = JaniApp.sharedPreferences.getBoolean("isFirstRun", true)
        val accessToken = JaniApp.authSharedPreferences.getString("accessToken", null)
        val refreshToken = JaniApp.authSharedPreferences.getString("refreshToken", null)
        Log.d("TOKEN", "Token in SharedPreferences: ${JaniApp.authSharedPreferences.getString("accessToken", null)}")

        if (isFirstRun) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            with(JaniApp.sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
            finish()
            return
        } else if (accessToken.isNullOrBlank() || refreshToken.isNullOrBlank()) {
            Log.d("TOKEN", "token exist in cash: ${!accessToken.isNullOrBlank()}")
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            viewModel.getUser()
        }
    }

    private fun setUpListeners() {
        binding.menuIcon.setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(binding.mainNavView)) {
                binding.drawerLayout.closeDrawer(binding.mainNavView)
            } else {
                binding.drawerLayout.openDrawer(binding.mainNavView)
            }
        }

        binding.profileImage.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.mainNavView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    replaceFragment(R.id.homeFragment, menuItem.title.toString())
                    true
                }
                R.id.nav_bmi -> {
                    replaceFragment(R.id.nav_bmi_graph, menuItem.title.toString())
                    true
                }
                R.id.nav_meal_plan -> {
                    replaceFragment(R.id.nav_meal_plan_graph, menuItem.title.toString())
                    true
                }
                R.id.nav_diary -> {
                    replaceFragment(R.id.nav_note_graph, menuItem.title.toString())
                    true
                }
                R.id.nav_products -> {
                    replaceFragment(R.id.productsFragment, menuItem.title.toString())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragmentId: Int, title: String) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(fragmentId)

        binding.drawerLayout.closeDrawers()
        setTitle(title)
    }

    private fun fragmentCheck() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateDrawerSelection(navController, binding.mainNavView, fragmentToMenuMap)
            if (destination.id == R.id.bmiAdditionalFragment ||
                destination.id == R.id.singleProductFragment ||
                destination.id == R.id.addNoteFragment ||
                destination.id == R.id.updateNoteFragment ||
                destination.id == R.id.mealPlanningResultFragment) {

                binding.menuIcon.visibility = View.GONE
                binding.profileImage.visibility = View.GONE
            } else {
                binding.menuIcon.visibility = View.VISIBLE
                binding.profileImage.visibility = View.VISIBLE
            }
        }
    }

    private fun updateDrawerSelection(navController: NavController, navigationView: NavigationView, fragmentToMenuMap: Map<Int, Int>) {
        val destinationId = navController.currentDestination?.id ?: return
        val menuItemId = fragmentToMenuMap[destinationId]

        Log.d("MENU_ID", "ID: $menuItemId")

        if (menuItemId != null) {
            navigationView.setCheckedItem(menuItemId)
        } else {
            // მოხსნას ყველა მონიშვნა
            for (i in 0 until navigationView.menu.size) {
                navigationView.menu[i].isChecked = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        return navHostFragment.navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}