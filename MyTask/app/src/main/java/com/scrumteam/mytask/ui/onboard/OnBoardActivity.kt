package com.scrumteam.mytask.ui.onboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.ActivityOnBoardBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import javax.inject.Inject

class OnBoardActivity : AppCompatActivity() {
    private var _binding: ActivityOnBoardBinding? = null
    private val binding get() = _binding as ActivityOnBoardBinding

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var loadingDialog: LoadingDialog

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        hideSystemUi()

        loadingDialog = LoadingDialog(this)



        setUpSlideOnBoard()
    }

    private fun setUpSlideOnBoard() {
        TODO("Not yet implemented")
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