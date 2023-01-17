package com.scrumteam.mytask.ui.personal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListTaskAdapter
import com.scrumteam.mytask.data.mapper.toLocalDateTime
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.databinding.FragmentPersonalBinding
import com.scrumteam.mytask.ui.MainActivity
import com.scrumteam.mytask.utils.StatusSnackBar
import com.scrumteam.mytask.utils.UiText
import com.scrumteam.mytask.utils.showSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding as FragmentPersonalBinding

    private val personalViewModel: PersonalViewModel by viewModels()

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
                    personalViewModel.checkedTask(it.copy(checked = true))
                }
            }
        )
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

        personalViewModel.personalState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                val tasks = state.personalTask
                showMessageState(tasks.isEmpty(), state.message)
                listTaskAdapter.submitList(state.personalTask)
                binding.recyclerTask.post { binding.recyclerTask.scrollToPosition(0) }
            } else {
                showMessageState(true, state.message)
                state.message?.let {
                    showSnackbar(binding.root, getString((it as UiText.StringResource).id),StatusSnackBar.DANGER)
                }
            }
        }

        personalViewModel.personalCheckedState.observe(viewLifecycleOwner) {
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

        setupRecyclerTask()
        act.showBottomSheetTask(destinationId = findNavController().currentDestination?.id)
    }

    private fun setupRecyclerTask() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerTask.apply {
            layoutManager = linearLayoutManager
            adapter = listTaskAdapter
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
                        act.setupBottomSheetDeleteTask { personalViewModel.deletePersonalTask(task) }
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

    private fun showMessageState(isShow: Boolean, message: UiText? = null) {
        if (isShow) {
            binding.recyclerTask.isVisible = false
            binding.tvPlaceholderState.apply {
                isVisible = true
                message?.let {
                    text = getString((it as UiText.StringResource).id)
                }
            }
        } else {
            binding.recyclerTask.isVisible = true
            binding.tvPlaceholderState.isVisible = false
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