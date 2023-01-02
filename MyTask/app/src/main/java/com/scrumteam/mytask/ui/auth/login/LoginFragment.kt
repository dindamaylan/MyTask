package com.scrumteam.mytask.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.FragmentLoginBinding
import com.scrumteam.mytask.ui.auth.AuthActivity
import com.scrumteam.mytask.utils.Helpers.isNotValidEmail
import com.scrumteam.mytask.utils.UiText
import com.scrumteam.mytask.utils.hideSoftKeyboard
import com.scrumteam.mytask.utils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding as FragmentLoginBinding

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var authAct: AuthActivity

    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnLogin.setOnClickListener {
                setToLogin()
//                navigateToHome()
            }
            btnLoginWithGoogle.setOnClickListener {
                authAct.signInWithGoogle()
            }
            tvRegister.setOnClickListener {
                navigateToRegister()
            }
        }
    }

    private fun setToLogin() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()

        if (email.isBlank()) {
            binding.layoutEdtEmail.apply {
                error = getString(
                    R.string.text_message_error_field_cant_empty,
                    getString(R.string.email)
                )
                isErrorEnabled = true
            }
        }
        if (password.isBlank()) {
            binding.layoutEdtPassword.apply {
                error = getString(
                    R.string.text_message_error_field_cant_empty,
                    getString(R.string.password)
                )
                isErrorEnabled = true
            }
        }
        val loginCorrect = !email.isNotValidEmail() && password.length >= 6

        if (loginCorrect) {
            loginViewModel.apply {
                login(email, password)
                loginUiState.observe(viewLifecycleOwner) { state ->
                    when {
                        state.isSuccess -> {
                            loadingDialog.hideDialog()
                        }
                        state.isLoading -> {
                            loadingDialog.showDialog()
                        }
                        state.isError -> {
                            loadingDialog.hideDialog()
                            showSnackBar(
                                requireActivity(),
                                getString((state.message as UiText.StringResource).id),
                                binding.root
                            )
                        }
                    }
                }
            }
        }

        requireActivity().currentFocus?.let {
            hideSoftKeyboard(requireContext(), it)
        }
    }

//    private fun navigateToHome() {
//        findNavController().navigate(R.id.action_loginFragment_to_home_nav)
//    }

    private fun navigateToRegister() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}