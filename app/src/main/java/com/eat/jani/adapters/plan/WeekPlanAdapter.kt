package com.eat.jani.adapters.plan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eat.jani.databinding.WeekPlanItemBinding
import com.eat.jani.models.calculations.DietPlanResponse

class WeekPlanAdapter(
    private val weeks: List<DietPlanResponse>,
    private val context: Context
) : RecyclerView.Adapter<WeekPlanAdapter.WeekPlanViewHolder>() {

    private val expandedStates = mutableMapOf<Int, Boolean>()

    inner class WeekPlanViewHolder(val binding: WeekPlanItemBinding) : RecyclerView.ViewHolder(binding.root) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekPlanViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WeekPlanItemBinding.inflate(inflater, parent, false)
        return WeekPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekPlanViewHolder, position: Int) {
        val week = weeks[position]
        val isExpanded = expandedStates[position] ?: false

        holder.binding.weekPlan = week
        holder.binding.isExpanded = isExpanded

        // MealsAdapter-ის დაყენება
        val mealAdapter = MealAdapter(week.mealPlan.meals)
        holder.binding.mealsRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.binding.mealsRecyclerView.adapter = mealAdapter

        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = weeks.size
}