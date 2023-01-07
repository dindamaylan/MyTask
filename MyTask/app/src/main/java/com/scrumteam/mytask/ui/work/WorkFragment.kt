package com.scrumteam.mytask.ui.work

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.scrumteam.mytask.R
import com.scrumteam.mytask.databinding.FragmentWorkBinding
import com.scrumteam.mytask.ui.MainActivity

class WorkFragment : Fragment() {
    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding as FragmentWorkBinding

    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkBinding.inflate(layoutInflater, container, false)
        act.window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.purple_task)
        binding.layoutToolbar.appbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.purple_task
            )
        )
        binding.layoutToolbar.toolbar.apply {
            title = getString(R.string.work)
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