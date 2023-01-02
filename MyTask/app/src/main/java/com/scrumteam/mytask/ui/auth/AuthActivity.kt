package com.scrumteam.mytask.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.ActivityAuthBinding
import com.scrumteam.mytask.ui.auth.login.LoginViewModel
import com.scrumteam.mytask.ui.onboard.OnBoardActivity
import com.scrumteam.mytask.ui.onboard.OnBoardViewModel
import com.scrumteam.mytask.utils.UiText
import com.scrumteam.mytask.utils.observeOnce
import com.scrumteam.mytask.utils.showSnackBar
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
        setContentView(R.layout.activity_auth)

        splashScreen.apply {
            setKeepOnScreenCondition {
                loginViewModel.currentUser.value != null
            }
        }

        onBoardViewModel.isFirstRunOnBoard.observe(this) { isFirstRun ->
            onBoardViewModel.updateIsFirstRunOnBoard(false)
            if (isFirstRun) {
                val intent = Intent(this, OnBoardActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_auth) as NavHostFragment
        navController = navHostFragment.navController

    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { token ->
                    val credential = GoogleAuthProvider.getCredential(token, null)
                    loginViewModel.loginWithGoogle(credential)
                    loginViewModel.loginUiState.observe(this@AuthActivity) { state ->
                        when {
                            state.isError -> {
                                loadingDialog.hideDialog()
                                showSnackBar(
                                    this@AuthActivity,
                                    getString((state.message as UiText.StringResource).id),
                                    binding.root
                                )
                            }
                            state.isLoading -> loadingDialog.showDialog()
                            state.isSuccess -> loadingDialog.hideDialog()

                        }
                    }
                    loginViewModel.checkUserIsExists.observeOnce(this) { exists ->
                        if (!exists) {
                            setToRegist()
                        }
                    }
                }
            } catch (e: ApiException) {
                showSnackBar(
                    this,
                    getString(R.string.text_message_error_something),
                    binding.root
                )
            }
        }
    }

    private fun setToRegist() {
        loginViewModel.addUser().observe(this@AuthActivity) { state ->
            when {
                state.isError -> {
                    loadingDialog.hideDialog()
                    showSnackBar(
                        this@AuthActivity,
                        getString((state.message as UiText.StringResource).id),
                        binding.root
                    )
                }
                state.isLoading -> loadingDialog.showDialog()
                state.isSuccess -> loadingDialog.hideDialog()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}