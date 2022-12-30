package com.scrumteam.mytask.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.ActivityAuthBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import javax.inject.Inject

class AuthActivity : AppCompatActivity() {

    private var _binding: ActivityAuthBinding? = null
    private val binding get() = _binding as ActivityAuthBinding

    private lateinit var navController: NavController

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var loadingDialog: LoadingDialog

    private val onBoardViewModel: OnBoardViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        splashScreen.apply {
            setKeepOnScreenCondition {
                loginViewModel.currentUser.value != null
            }

        }

        setContentView(R.layout.activity_auth)
    }


}