package com.eat.jani.fragments.main.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eat.jani.R
import com.eat.jani.adapters.products.ProductAdapter
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentProductsBinding
import com.eat.jani.repositories.products.ProductsRepository
import com.eat.jani.viewmodels.ProductsViewModelFactory
import com.eat.jani.viewmodels.products.ProductsViewModel

class ProductsFragment : Fragment() {
    private lateinit var binding: FragmentProductsBinding
    private lateinit var adapter: ProductAdapter
    private val viewModel: ProductsViewModel by viewModels {
        ProductsViewModelFactory(ProductsRepository(RetrofitInstance.productsApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        adapter = ProductAdapter(emptyList()) {
            val bundle = Bundle().apply {
                putParcelable("product", it)
            }
            findNavController().navigate(R.id.action_productsFragment_to_singleProductFragment, bundle)
        }
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = adapter

        setUpSpinner()
        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setUpSpinner() {
        val adapter = object : ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item,
            viewModel.categoryDisplay
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return createCustomView(position, convertView, parent)
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.spinner_dropdown_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerDropDownText)
                textView.text = getItem(position)
                return view
            }

            private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.spinner_item, parent, false)
                val textView = view.findViewById<TextView>(R.id.spinnerText)
                textView.text = getItem(position)
                return view
            }
        }

        binding.spinner.adapter = adapter
    }

    private fun setUpListeners() {
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedText = viewModel.categoryDisplay[position]
                viewModel.setCategory(selectedText)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // არ არის საჭირო
            }
        }
    }

    private fun setUpObservers() {
        viewModel.searchInput.observe(viewLifecycleOwner) {
            viewModel.searchProducts()
        }
        viewModel.success.observe(viewLifecycleOwner) { success ->
            if (success) {
                binding.searchAlertLayout.visibility = View.GONE
                adapter.updateList(viewModel.products.value ?: emptyList())
            } else {
                binding.searchAlertLayout.visibility = View.VISIBLE
                adapter.updateList(emptyList())
            }
        }
        viewModel.generalError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}