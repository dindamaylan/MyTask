package com.scrumteam.mytask.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.scrumteam.mytask.data.model.boarding.Boarding
import com.scrumteam.mytask.databinding.ItemRowOnBoardingBinding

class ListBoardingAdapter :
    ListAdapter<Boarding, ListBoardingAdapter.ListBoardingViewHolder>(DiffCallback) {
    private lateinit var context: Context

    inner class ListBoardingViewHolder(
        private val binding: ItemRowOnBoardingBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(boarding: Boarding) {
            binding.apply {
                ivBoarding.setImageResource(boarding.iconRes)
                tvTitleBoarding.text = context.getString(boarding.titleRes)
                tvDescBoarding.text = context.getString(boarding.descRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBoardingViewHolder {
        context = parent.context
        val binding =
            ItemRowOnBoardingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ListBoardingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListBoardingViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Boarding>() {
        override fun areItemsTheSame(oldItem: Boarding, newItem: Boarding): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Boarding, newItem: Boarding): Boolean {
            return oldItem.iconRes == newItem.iconRes
        }
    }
}