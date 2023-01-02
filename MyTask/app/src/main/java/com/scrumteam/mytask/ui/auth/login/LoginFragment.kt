package com.scrumteam.mytask.ui.auth.login

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding as FragmentLoginBinding

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var loadingDialog: LoadingDialog

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel.loginState.observe(viewLifecycleOwner) { state ->
            when {
                state.isError -> Toast.makeText(
                    requireContext(), "Error", Toast.LENGTH_SHORT
                ).show()
                state.isLoading -> Toast.makeText(
                    requireContext(), "Loading", Toast.LENGTH_SHORT
                ).show()
                state.currentUser != null -> Toast.makeText(
                    requireContext(), "Success", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.apply {
            tvRegister.setOnClickListener {
                navigateToRegister()
            }

            btnLoginWithGoogle.setOnClickListener {
                setupLoginWithGoogle()
            }
        }

        setupLoginWithEmailPassword()
    }

    private fun setupLoginWithEmailPassword() {
        binding.btnLogin.isEnabled = false

        val email = binding.edtEmail
        val password = binding.edtPassword

        var emailText = ""
        var passwordText = ""

        var correctEmail = false
        var correctPassword = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                when {
                    editable === email.editableText -> {
                        emailText = email.text.toString()
                        correctEmail = emailText.isNotEmpty()
                    }

                    editable === password.editableText -> {
                        passwordText = password.text.toString()
                        correctPassword = passwordText.isNotEmpty()
                    }
                }
                binding.btnLogin.isEnabled = correctEmail && correctPassword
            }
        }

        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        binding.btnLogin.setOnClickListener {
            loginViewModel.loginWithEmailPassword(emailText, passwordText)
        }

    }

    private fun setupLoginWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.result
                    account.idToken?.let { idToken ->
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        loginViewModel.loginWithCredential(credential)
                    } ?: Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d("TAG", "Error: ")
            }
        }


    private fun navigateToRegister() {
        val direction =
            LoginFragmentDirections.actionLoginNavToRegisterFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}