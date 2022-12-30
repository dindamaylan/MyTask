package com.scrumteam.mytask.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.ActivityMainBinding
import com.scrumteam.mytask.ui.onboard.OnBoardActivity
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import com.scrumteam.mytask.utils.margin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val onBoardViewModel: OnBoardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onBoardViewModel.isFirstRunOnBoard.observe(this) { isFirst ->
            onBoardViewModel.updateIsFirstRunOnBoard(false)
            if (isFirst) {
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigation(navController)
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
}