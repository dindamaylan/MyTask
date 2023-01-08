package com.scrumteam.mytask.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.FragmentPersonalBinding
import com.scrumteam.mytask.ui.MainActivity

class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding as FragmentPersonalBinding

    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(layoutInflater, container, false)
        act.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.blue_task)
        binding.layoutToolbar.appbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.blue_task
            )
        )
        binding.layoutToolbar.toolbar.apply {
            title = getString(R.string.personal)
            navigationIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrow_back)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutToolbar.toolbar.setNavigationOnClickListener {
            navigateToBack()
        }
    }

    private fun navigateToBack() {
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        act.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)
        _binding = null
    }
}