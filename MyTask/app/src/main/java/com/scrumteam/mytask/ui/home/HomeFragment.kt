package com.scrumteam.mytask.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.userState.observe(viewLifecycleOwner) { state ->
            when {
                state.isError -> Log.d("TAG", "onViewCreated: Eror")
                state.firebaseUser != null -> {
                    setupViewUser(state.firebaseUser)
                }
            }
        }
    }

    private fun setupViewUser(firebaseUser: FirebaseUser) {
        binding.apply {
            tvUsername.text = firebaseUser.displayName
            if (firebaseUser.photoUrl != null){
                ivAvatar.load(firebaseUser.photoUrl)
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}