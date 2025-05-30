package com.eat.jani.fragments.main.products

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.eat.jani.R
import com.eat.jani.adapters.BindingAdapters
import com.eat.jani.databinding.FragmentSingleProductBinding
import com.eat.jani.models.products.Product

class SingleProductFragment : Fragment() {
    private lateinit var binding: FragmentSingleProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_product, container, false)

        val product = arguments?.getParcelable<Product>("product")
        product?.let {
            binding.product = it
            when(it.category) {
                "Protein" -> binding.productCategoryText.text = "ცილები"
                "Carb" -> binding.productCategoryText.text = "ნახშირწყლები"
                "Vegetable" -> binding.productCategoryText.text = "ბოსტნეული"
                "Fruit" -> binding.productCategoryText.text = "ხილი"
                else -> ""
            }
        }

        binding.arrowBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return binding.root
    }
}