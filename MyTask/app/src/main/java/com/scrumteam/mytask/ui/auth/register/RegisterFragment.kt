package com.scrumteam.mytask.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.scrumteam.mytask.databinding.FragmentRegisterBinding
import com.scrumteam.mytask.utils.hideSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding as FragmentRegisterBinding

    private val registerViewModel: RegisterViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener {
            navigateToBack()
        }

        setupRegister()
    }

    private fun setupRegister() {
        binding.btnRegister.isEnabled = false

        val firstName = binding.edtFirstName
        val lastName = binding.edtLastName
        val email = binding.edtEmail
        val password = binding.edtPassword

        var firstNameText = ""
        var lastNameText = ""
        var emailText = ""
        var passwordText = ""

        var correctFirstName = false
        var correctLastName = false
        var correctEmail = false
        var correctPassword = false

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                when {
                    editable === firstName.editableText -> {
                        firstNameText = firstName.text.toString()
                        correctFirstName = firstNameText.isNotEmpty()
                    }
                    editable === lastName.editableText -> {
                        lastNameText = lastName.text.toString()
                        correctLastName = lastNameText.isNotEmpty()
                    }
                    editable === email.editableText -> {
                        emailText = email.text.toString()
                        correctEmail = emailText.isNotEmpty()
                    }

                    editable === password.editableText -> {
                        passwordText = password.text.toString()
                        correctPassword = passwordText.isNotEmpty()
                    }
                }
                binding.btnRegister.isEnabled =
                    correctEmail && correctPassword && correctFirstName && correctLastName
            }
        }

        firstName.addTextChangedListener(textWatcher)
        lastName.addTextChangedListener(textWatcher)
        email.addTextChangedListener(textWatcher)
        password.addTextChangedListener(textWatcher)

        binding.btnRegister.setOnClickListener {
            registerViewModel.registerWithEmailPassword(
                firstNameText,
                lastNameText,
                emailText,
                passwordText
            )
            requireActivity().currentFocus?.let {
                hideSoftKeyboard(requireContext(), it)
            }
        }
    }

    private fun navigateToBack() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}