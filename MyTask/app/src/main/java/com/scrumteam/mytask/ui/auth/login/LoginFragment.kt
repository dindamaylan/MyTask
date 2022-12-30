package com.scrumteam.mytask.ui.auth.login

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.scrumteam.mytask.R
import com.scrumteam.mytask.custom.components.LoadingDialog
import com.scrumteam.mytask.databinding.FragmentLoginBinding
import com.scrumteam.mytask.ui.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() =_binding as FragmentLoginBinding

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var authAct: AuthActivity

    private val loginViewModel: LoginViewModel by viewModels()

    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}