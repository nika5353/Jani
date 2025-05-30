package com.eat.jani.repositories.note

import com.eat.jani.api.note.NoteApiService
import com.eat.jani.models.note.MessageResponce
import com.eat.jani.models.note.Note
import com.eat.jani.models.note.NoteDTO
import retrofit2.Response

class NoteRepository(private val api: NoteApiService) {
    suspend fun fetchNotes() : Response<List<Note>?> {
        return api.fetchNotes()
    }

    suspend fun fetchSingleNote(path: String) : Response<NoteDTO> {
        return api.fetchSingleNote(path)
    }

    suspend fun searchNoteItems(query: String) : Response<List<Note>?> {
        return api.searchNoteItems(query)
    }

    suspend fun createNewNote(request: NoteDTO) : Response<MessageResponce> {
        return api.createNewNote(request)
    }

    suspend fun updateExistingNote(path: String, request: NoteDTO) : Response<MessageResponce> {
        return api.updateExistingNote(path, request)
    }

    suspend fun deleteExistingNote(path: String) : Response<MessageResponce> {
        return api.deleteExistingNote(path)
    }
}