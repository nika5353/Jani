package com.eat.jani.adapters.products

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eat.jani.databinding.ProductItemBinding
import com.eat.jani.models.products.Product

class ProductAdapter(
    private var products: List<Product>?,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ProductItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.product = product
            binding.root.setOnClickListener {
                onProductClick(product)
            }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductItemBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        products?.let { holder.bind(it[position]) }
    }

    override fun getItemCount(): Int = products?.size!!

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}