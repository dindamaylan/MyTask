package com.scrumteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scrumteam.mytask.R
import com.scrumteam.mytask.data.mapper.getLocalDateFormat
import com.scrumteam.mytask.data.mapper.toLocalDateTime
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.databinding.ItemRowTaskBinding
import com.scrumteam.mytask.utils.Constants.DATE_FORMATTER
import com.scrumteam.mytask.utils.Constants.TIME_FORMATTER
import com.scrumteam.mytask.utils.mergeDateTimeWithCategoryTask
import com.scrumteam.mytask.utils.toTaskCodeRes

class ListTaskAdapter(
    private val onActionItem: (task: Task, actionView: ImageButton) -> Unit,
    private val onCompleteItem: (Task) -> Unit
) : ListAdapter<Task, ListTaskAdapter.ListTaskViewHolder>(DiffCallback) {
    private lateinit var ctx: Context

    inner class ListTaskViewHolder(
        private val binding: ItemRowTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.apply {
                tvTitleTask.text = task.title
                tvDateWithCategoryTask.text =
                    mergeDateTimeWithCategoryTask(
                        getLocalDateFormat(DATE_FORMATTER, task.date.toLocalDateTime()),
                        getLocalDateFormat(TIME_FORMATTER, task.time.toLocalDateTime()),
                        task.category.toTaskCodeRes(ctx)
                    )

                checkTask.apply {
                    isChecked = task.checked
                    isEnabled = !task.checked
                }

                if (task.checked) {
                    root.setCardForegroundColor(
                        ContextCompat.getColorStateList(
                            ctx,
                            R.color.gray_translucent
                        )
                    )
                }else {
                    root.setCardForegroundColor(null)
                }


                checkTask.setOnClickListener {
                    checkTask.isChecked = task.checked
                    onCompleteItem(task)
                }

                btnMore.apply {
                    isEnabled = !task.checked
                    setOnClickListener {
                        onActionItem(task, btnMore)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListTaskViewHolder {
        ctx = parent.context
        val binding = ItemRowTaskBinding.inflate(LayoutInflater.from(ctx), parent, false)
        return ListTaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListTaskViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}