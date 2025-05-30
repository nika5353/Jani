package com.eat.jani.fragments.main.note

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eat.jani.R
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentAddNoteBinding
import com.eat.jani.repositories.note.NoteRepository
import com.eat.jani.viewmodels.NoteViewModelFactory
import com.eat.jani.viewmodels.note.NoteViewModel
import kotlin.getValue

class AddNoteFragment : Fragment() {
    private lateinit var binding: FragmentAddNoteBinding
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(NoteRepository(RetrofitInstance.noteApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_note, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setUpObservers() {
        viewModel.addNoteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                findNavController().popBackStack()
            }
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.loadingAnimation.visibility = if (isLoading) {
                binding.loadingAnimation.playAnimation()
                View.VISIBLE
            } else {
                binding.loadingAnimation.cancelAnimation()
                View.GONE
            }
        }
        viewModel.generalError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpListeners() {
        binding.arrowBack.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }
}