package com.scrumteam.mytask.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.scrumteam.mytask.MainNavGraphDirections
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.ActivityMainBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import com.scrumteam.mytask.ui.onboard.OnBoardActivity
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import com.scrumteam.mytask.utils.margin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val onBoardViewModel: OnBoardViewModel by viewModels()

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            onBoardViewModel.isFirstRunOnBoard.value == null
        }

        onBoardViewModel.isFirstRunOnBoard.observe(this) { isFirst ->
            onBoardViewModel.updateIsFirstRunOnBoard(false)
            if (isFirst) {
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigation(navController)

        loginViewModel.currentUser.observe(this) { currentUser ->
            if (currentUser == null) {
                navigateToLogin(navController)
            } else {
                navigateToHome(navController)
            }
        }
    }

    private fun setupBottomNavigation(navController: NavController) {
        binding.bottomNav.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.home_nav &&
                destination.id != R.id.calendar_nav &&
                destination.id != R.id.notification_nav &&
                destination.id != R.id.profile_nav
            ) {
                binding.apply {
                    fragmentContainerView.margin(bottom = 0f)
                    bottomNav.isVisible = false
                    fabTask.hide()
                }
            } else {
                binding.apply {
                    fragmentContainerView.margin(bottom = 56f)
                    bottomNav.isVisible = true
                    fabTask.show()
                }
            }
        }
    }

    private fun navigateToLogin(navController: NavController) {
        val direction = MainNavGraphDirections.actionToLoginNav()
        navController.navigate(direction)
    }

    private fun navigateToHome(navController: NavController) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.main_nav_graph, true)
            .setLaunchSingleTop(true)
            .build()
        navController.navigate(R.id.home_nav, null, navOptions)
    }
}