package com.eat.jani.fragments.main.note

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eat.jani.R
import com.eat.jani.adapters.note.NoteAdapter
import com.eat.jani.api.RetrofitInstance
import com.eat.jani.databinding.FragmentNoteBinding
import com.eat.jani.repositories.note.NoteRepository
import com.eat.jani.viewmodels.NoteViewModelFactory
import com.eat.jani.viewmodels.note.NoteViewModel

class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var adapter: NoteAdapter
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(NoteRepository(RetrofitInstance.noteApi))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_note, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getNotes()
        binding.noteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setUpRecyclerView()
        setUpObservers()
        setUpListeners()

        return binding.root
    }

    private fun setUpRecyclerView() {
        adapter = NoteAdapter(
            emptyList(),
            onCardClick = { note ->
                val bundle = Bundle().apply {
                    putString("noteId", note.id)
                }
                findNavController().navigate(R.id.action_noteFragment_to_updateNoteFragment, bundle)
            },
            onDeleteClick = { note ->
                AlertDialog.Builder(requireContext())
                    .setTitle("წაშლა")
                    .setMessage("დარწმუნებული ხარ, რომ გინდა წაშალო?")
                    .setIcon(R.drawable.delete)
                    .setPositiveButton("დიახ") { dialog, _ ->
                        viewModel.deleteNote(note.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("არა") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        )
        binding.noteRecyclerView.adapter = adapter
    }

    private fun setUpObservers() {
        viewModel.notesList.observe(viewLifecycleOwner) {
            adapter.updateList(it ?: emptyList())
        }
        viewModel.searchInput.observe(viewLifecycleOwner) {
            viewModel.searchNotes()
        }
        viewModel.deleteSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                val input = viewModel.searchInput.value
                if (input!!.isBlank()) {
                    viewModel.getNotes()
                } else {
                    viewModel.searchNotes()
                }
            }
        }
        viewModel.generalError.observe(viewLifecycleOwner) {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpListeners() {
        binding.addNoteBtn.setOnClickListener {
            findNavController().navigate(R.id.action_noteFragment_to_addNoteFragment)
        }
    }
}