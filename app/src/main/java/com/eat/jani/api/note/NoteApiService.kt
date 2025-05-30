package com.eat.jani.api.note

import com.eat.jani.models.note.MessageResponce
import com.eat.jani.models.note.Note
import com.eat.jani.models.note.NoteDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NoteApiService {
    @GET("Notes/get-notes")
    suspend fun fetchNotes() : Response<List<Note>?>

    @GET("Notes/get-notes/{noteId}")
    suspend fun fetchSingleNote(@Path("noteId") path: String) : Response<NoteDTO>

    @GET("Notes/search-notes")
    suspend fun searchNoteItems(@Query("query") query: String) : Response<List<Note>?>

    @POST("Notes/add-note")
    suspend fun createNewNote(@Body request: NoteDTO) : Response<MessageResponce>

    @PUT("Notes/update-note/{noteId}")
    suspend fun updateExistingNote(@Path("noteId") path: String, @Body request: NoteDTO) : Response<MessageResponce>

    @DELETE("Notes/delete-note/{noteId}")
    suspend fun deleteExistingNote(@Path("noteId") path: String) : Response<MessageResponce>
}