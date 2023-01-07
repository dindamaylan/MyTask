package com.scrumteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scrumteam.mytask.data.model.task.Task
import com.scrumteam.mytask.databinding.ItemRowTaskBinding
import com.scrumteam.mytask.utils.mergeDateTimeWithCategoryTask
import com.scrumteam.mytask.utils.toTaskCodeRes

class ListTaskAdapter(
    private val onClickItem: (Task) -> Unit
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
                        task.date,
                        task.time,
                        task.category.toTaskCodeRes(ctx)
                    )
                checkTask.isChecked = task.isCheck
                root.isEnabled = task.isCheck
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
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }
    }
}