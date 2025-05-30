package com.eat.jani.adapters.plan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eat.jani.databinding.ProgressItemBinding
import com.eat.jani.models.calculations.ProgressResponse

class ProgressAdapter(
    private val weeks: List<ProgressResponse>
) : RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {

    private val expandedStates = mutableMapOf<Int, Boolean>()

    inner class ProgressViewHolder(val binding: ProgressItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val currentState = expandedStates[position] ?: false
                    expandedStates[position] = !currentState
                    notifyItemChanged(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProgressItemBinding.inflate(inflater, parent, false)
        return ProgressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        val week = weeks[position]
        val isExpanded = expandedStates[position] ?: false

        holder.binding.weekPlan = week
        holder.binding.isExpanded = isExpanded

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = weeks.size
}