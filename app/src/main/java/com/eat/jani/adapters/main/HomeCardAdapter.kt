package com.eat.jani.adapters.main

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eat.jani.R
import com.eat.jani.databinding.ItemHomeCardBinding
import com.eat.jani.models.main.HomeCard

class HomeCardAdapter(
    private var cards: List<HomeCard>,
    private val onClick: (HomeCard) -> Unit
) : RecyclerView.Adapter<HomeCardAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHomeCardBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.item_home_card,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.bind(cards[position])
    }

    override fun getItemCount(): Int = cards.size

    inner class MyViewHolder(private val binding: ItemHomeCardBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(card: HomeCard) {
            binding.cardTitle.text = card.title

            Glide.with(binding.root.context)
                .load(card.iconRes)
                .centerCrop()
                .into(binding.cardImage)

            binding.root.setOnClickListener {
                onClick(card)
            }
        }
    }
}