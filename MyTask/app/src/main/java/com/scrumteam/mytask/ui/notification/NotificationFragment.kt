package com.scrumteam.mytask.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.scrumteam.mytask.R
import com.scrumteam.mytask.adapter.ListNotificationAdapter
import com.scrumteam.mytask.databinding.FragmentNotificationBinding
import com.scrumteam.mytask.utils.setTextColorRes
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding as FragmentNotificationBinding

    private val notificationViewModel: NotificationViewModel by viewModels()

    private lateinit var listNotificationAdapter: ListNotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listNotificationAdapter = ListNotificationAdapter { notification ->
            notificationViewModel.readNotification(notification)
        }

        notificationViewModel.notificationState.observe(viewLifecycleOwner) { state ->
            if (!state.isError) {
                hasReadAllNotification(state.notifications.none { !it.read })
                listNotificationAdapter.submitList(state.notifications)
            }
        }

        setupRecyclerNotification()

        binding.tvReadAllNotification.setOnClickListener {
            notificationViewModel.readAllNotification()
        }
    }

    private fun hasReadAllNotification(hasReadAll: Boolean) {
        if (hasReadAll) {
            binding.tvReadAllNotification.apply {
                setTextColorRes(R.color.gray)
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_inactive),
                    null, null, null
                )
                isEnabled = false
            }
        } else {
            binding.tvReadAllNotification.apply {
                setTextColorRes(R.color.primary)
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_check),
                    null, null, null
                )
                isEnabled = true
            }
        }
    }

    private fun setupRecyclerNotification() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotification.apply {
            layoutManager = linearLayoutManager
            adapter = listNotificationAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}