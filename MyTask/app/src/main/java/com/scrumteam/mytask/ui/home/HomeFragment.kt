package com.scrumteam.mytask.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListTaskAdapter
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.databinding.FragmentHomeBinding
import com.scrumteam.mytask.utils.getTotalTaskByCategory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var listTaskAdapter: ListTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listTaskAdapter = ListTaskAdapter { }
    }

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
            if (state.firebaseUser != null) {
                setupViewUser(state.firebaseUser)
            }
        }

        homeViewModel.taskState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                setupCategoryTask(state.tasks)
                listTaskAdapter.submitList(state.tasks)
            }
        }

        setupRecyclerTask()
    }

    private fun setupRecyclerTask() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTask.apply {
            layoutManager = linearLayoutManager
            adapter = listTaskAdapter
        }
    }

    private fun setupViewUser(firebaseUser: FirebaseUser) {
        binding.apply {
            tvUsername.text = firebaseUser.displayName
            val avatar = if (firebaseUser.photoUrl != null) {
                firebaseUser.photoUrl
            } else {
                R.drawable.ic_avatar
            }
            ivAvatar.load(avatar)
        }
    }

    private fun setupCategoryTask(tasks: List<Task>) {
        binding.catTaskPersonal.apply {
            root.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_task))
            root.setOnClickListener { navigateToPersonalTask() }
            ivImageTask.setImageResource(R.drawable.illus_personal)
            tvTitleTask.text = getString(R.string.personal)
            tvTotalTask.text =
                getString(R.string.total_task, tasks.getTotalTaskByCategory(TaskCode.PERSONAL))
        }

        binding.catTaskWork.apply {
            root.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_task
                )
            )
            root.setOnClickListener { navigateToWorkTask() }
            ivImageTask.setImageResource(R.drawable.illus_work)
            tvTitleTask.text = getString(R.string.work)
            tvTotalTask.text =
                getString(R.string.total_task, tasks.getTotalTaskByCategory(TaskCode.WORK))
        }

        binding.catTaskSchool.apply {
            root.setCardBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange_task
                )
            )
            root.setOnClickListener { navigateToSchoolTask() }
            ivImageTask.setImageResource(R.drawable.illus_school)
            tvTitleTask.text = getString(R.string.school)
            tvTotalTask.text =
                getString(R.string.total_task, tasks.getTotalTaskByCategory(TaskCode.SCHOOL))
        }
    }

    private fun navigateToPersonalTask() {
        val direction = HomeFragmentDirections.actionHomeNavToPersonalNav()
        findNavController().navigate(direction)
    }

    private fun navigateToWorkTask() {
        val direction = HomeFragmentDirections.actionHomeNavToWorkNav()
        findNavController().navigate(direction)
    }

    private fun navigateToSchoolTask() {
        val direction = HomeFragmentDirections.actionHomeNavToSchoolNav()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}