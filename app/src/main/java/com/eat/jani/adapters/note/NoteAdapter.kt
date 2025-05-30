package com.eat.jani.adapters.note

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eat.jani.databinding.NoteItemBinding
import com.eat.jani.models.note.Note

class NoteAdapter(
    private var notes: List<Note>?,
    private val onCardClick: (Note) -> Unit,
    private val onDeleteClick: (Note) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    inner class NoteViewHolder(val binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.note = note
            binding.cardView.setOnClickListener {
                onCardClick(note)
            }
            binding.deleteBtn.setOnClickListener {
                onDeleteClick(note)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NoteItemBinding.inflate(inflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        notes?.let { holder.bind(it[position]) }
    }

    override fun getItemCount(): Int = notes?.size!!

    fun updateList(newList: List<Note>) {
        notes = newList
        notifyDataSetChanged()
    }
}