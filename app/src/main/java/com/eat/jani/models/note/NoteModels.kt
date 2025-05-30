package com.eat.jani.models.note

import com.google.gson.annotations.SerializedName

data class Note(
    @SerializedName("noteId")
    val id: String,
    val title: String,
    @SerializedName("summary")
    val desc: String,
    val updatedAt: String
)

data class MessageResponce(
    val state: String,
    val message: String
)

data class NoteDTO(
    val title: String,
    @SerializedName("content")
    val desc: String
)

