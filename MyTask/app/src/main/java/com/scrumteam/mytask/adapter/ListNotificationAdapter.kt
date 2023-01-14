package com.scrumteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.mapper.getLocalDateFormat
import com.scrumteam.mytask.data.mapper.toLocalDateTime
import com.scrumteam.mytask.data.model.notification.Notification
import com.scrumteam.mytask.databinding.ItemRowNotificationBinding
import com.scrumteam.mytask.utils.Constants
import com.scrumteam.mytask.utils.mergeDateTimeWithCategoryTask
import com.scrumteam.mytask.utils.setTextColorRes

class ListNotificationAdapter(
    private val onClickItem: (Notification) -> Unit
) : ListAdapter<Notification, ListNotificationAdapter.ListNotificationViewHolder>(DiffCallback) {
    private lateinit var ctx: Context

    inner class ListNotificationViewHolder(
        private val binding: ItemRowNotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.apply {
                if (notification.read) {
                    ivCircle.setImageResource(R.drawable.ic_circle_inactive)
                    tvMessage.setTextColorRes(R.color.black)
                } else {
                    ivCircle.setImageResource(R.drawable.ic_circle_active)
                    tvMessage.setTextColorRes(R.color.primary)
                }
                tvMessage.text = notification.message
                tvDate.text = mergeDateTimeWithCategoryTask(
                    getLocalDateFormat(
                        Constants.DATE_FORMATTER,
                        notification.date.toLocalDateTime()
                    ),
                    getLocalDateFormat(
                        Constants.TIME_FORMATTER,
                        notification.time.toLocalDateTime()
                    ),
                    ""
                )

                if (!notification.read){
                    root.setOnClickListener {
                        onClickItem(notification)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListNotificationViewHolder {
        ctx = parent.context
        val binding = ItemRowNotificationBinding.inflate(LayoutInflater.from(ctx), parent, false)
        return ListNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListNotificationViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Notification>() {
        override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
            return oldItem.id == newItem.id
        }

    }
}