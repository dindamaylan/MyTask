package com.scrumteam.mytask.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.firebase.auth.FirebaseUser
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListTaskAdapter
import com.scrumteam.mytask.data.mapper.toLocalDateTime
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.data.model.task.TaskCode
import com.scrumteam.mytask.databinding.FragmentHomeBinding
import com.scrumteam.mytask.ui.MainActivity
import com.scrumteam.mytask.utils.StatusSnackBar
import com.scrumteam.mytask.utils.UiText
import com.scrumteam.mytask.utils.getTotalTaskByCategory
import com.scrumteam.mytask.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var listTaskAdapter: ListTaskAdapter
    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as MainActivity
        listTaskAdapter = ListTaskAdapter(
            onActionItem = { task, actionView ->
                setupPopupActionMenu(task, actionView)
            },
            onCompleteItem = {
                act.setupBottomSheetCheckedTask {
                    homeViewModel.checkedTask(it)
                }
            }
        )
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

        binding.searchTask.clearFocus()

        homeViewModel.userState.observe(viewLifecycleOwner) { state ->
            if (state.firebaseUser != null) {
                setupViewUser(state.firebaseUser)
            }
        }

        homeViewModel.taskState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                setupCategoryTask(state.tasks)
            }
        }

        homeViewModel.taskFilterState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                listTaskAdapter.submitList(state.tasks)
            }
        }

        homeViewModel.taskCheckedState.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { state ->
                when {
                    state.isError -> showSnackbar(
                        binding.root,
                        getString((state.message as UiText.StringResource).id),
                        StatusSnackBar.DANGER
                    )
                    state.isSuccess -> showSnackbar(
                        binding.root,
                        getString((state.message as UiText.StringResource).id),
                        StatusSnackBar.SUCCESS
                    )
                }
            }
        }

        act.showBottomSheetTask(destinationId = findNavController().currentDestination?.id)
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

    private fun setupPopupActionMenu(task: Task, actionView: ImageButton) {
        var popoupMenu: PopupMenu? = null
        if (popoupMenu == null) {
            popoupMenu = PopupMenu(requireContext(), actionView)
            popoupMenu.menuInflater.inflate(R.menu.action_task_menu, popoupMenu.menu)
            popoupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.delete_task -> {
                        act.setupBottomSheetDeleteTask { homeViewModel.deleteTask(task) }
                    }
                    R.id.edit_task -> {
                        act.setupBottomSheetAddTask(
                            isUpdate = true,
                            destinationId = findNavController().currentDestination?.id,
                            taskId = task.id,
                            userId = task.userId,
                            titleTaskNew = task.title,
                            dateTaskNew = task.date.toLocalDateTime(),
                            timeTaskNew = task.time.toLocalDateTime()
                        )
                    }
                }
                false
            }

            popoupMenu.show()

            popoupMenu.setOnDismissListener {
                popoupMenu = null
            }

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