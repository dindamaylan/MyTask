package com.scrumteam.mytask.ui.onboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListBoardingAdapter
import com.scrumteam.mytask.data.model.boarding.Boarding
import com.scrumteam.mytask.databinding.ActivityOnBoardBinding
import com.scrumteam.mytask.ui.MainActivity

class OnBoardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUi()

        setupOnBoarding()
    }

    private fun setupOnBoarding() {
        val boardingOne =
            Boarding(R.drawable.illus_boarding_1, R.string.onboard_1_1, R.string.onboard_1_2)
        val boardingTwo =
            Boarding(R.drawable.illus_boarding_2, R.string.onboard_2_1, R.string.onboard_2_2)
        val boardingThree =
            Boarding(R.drawable.illus_boarding_3, R.string.onboard_3_1, R.string.onboard_3_2)

        val listBoarding = listOf(boardingOne, boardingTwo, boardingThree)

        val listBoardingAdapter = ListBoardingAdapter()
        listBoardingAdapter.submitList(listBoarding)

        val changePage = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handleNavigationBoarding(position, listBoarding.size - 1)
            }
        }

        binding.vpOnboard.apply {
            adapter = listBoardingAdapter
            registerOnPageChangeCallback(changePage)
            binding.indicatorOnBoard.attachTo(this)
        }
    }

    private fun handleNavigationBoarding(position: Int, lastPage: Int) {
        if (position == lastPage) {
            binding.apply {
                indicatorOnBoard.isVisible = false
                btnSkip.visibility = View.INVISIBLE
                btnStart.isVisible = true
            }
        } else {
            binding.apply {
                indicatorOnBoard.isVisible = true
                btnSkip.visibility = View.VISIBLE
                btnStart.isVisible = false
            }
        }

        binding.apply {
            btnSkip.setOnClickListener {
                vpOnboard.currentItem = position + 1
            }
            btnStart.setOnClickListener {
                navigateToMain()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun hideSystemUi() {
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        val statusBar = WindowInsetsCompat.Type.statusBars()
        val navBars = WindowInsetsCompat.Type.navigationBars()
        insetsController.hide(statusBar)
        insetsController.hide(navBars)
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}