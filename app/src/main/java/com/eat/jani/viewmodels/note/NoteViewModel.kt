package com.eat.jani.viewmodels.note

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.eat.jani.models.note.Note
import com.eat.jani.models.note.NoteDTO
import com.eat.jani.repositories.note.NoteRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import kotlin.coroutines.cancellation.CancellationException

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    val searchInput = MutableLiveData<String>("")
    val noteTitle = MutableLiveData<String>()
    val noteDesc = MutableLiveData<String>()

    private val _notesList = MutableLiveData<List<Note>?>()
    val notesList: LiveData<List<Note>?> = _notesList

    private val _singleNote = MutableLiveData<NoteDTO>()
    val singleNote: LiveData<NoteDTO> get() = _singleNote

    //States
    val isLoading = MutableLiveData<Boolean>(false)
    val addNoteSuccess = MutableLiveData<Boolean>(false)
    val searchSuccess = MutableLiveData<Boolean>(false)
    val updateSuccess = MutableLiveData<Boolean>(false)
    val deleteSuccess = MutableLiveData<Boolean>(false)

    //Error
    val generalError = MutableLiveData<String?>()

    // Job
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            searchInput.asFlow()
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest {
                    searchNotes()
                }
        }
    }


    fun getNotes() {
        generalError.value = null

        viewModelScope.launch {
            try {
                val response = repository.fetchNotes()
                if (response.isSuccessful) {
                    val notes = response.body() ?: emptyList()
                    val sortedNotes = notes.sortedByDescending { parseDate(it.updatedAt) }
                    _notesList.value = sortedNotes
                } else {
                    generalError.value = "მონაცემების წამოღება ვერ მოხდა"
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა"
                Log.e("NOTES", "Exception: ", e)
            }
        }
    }

    fun getSingleNote(noteId: String) {
        generalError.value = null

        viewModelScope.launch {
            try {
                val response = repository.fetchSingleNote(noteId)
                if (response.isSuccessful) {
                    _singleNote.value = response.body()
                } else {
                    generalError.value = "მიღება ვერ მოხდა"
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("NOTES", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun searchNotes() {
        generalError.value = null

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                // თუ ძიება სავალდებულოა, ცარიელზე არ გავუშვათ
                if (searchInput.value!!.isBlank()) {
                    searchSuccess.value = false
                    _notesList.value = repository.fetchNotes().body() ?: emptyList()
                    return@launch
                }

                val response = repository.searchNoteItems(searchInput.value!!)
                if (response.isSuccessful) {
                    searchSuccess.value = true
                    _notesList.value = response.body() ?: emptyList()
                } else {
                    searchSuccess.value = false

                    _notesList.value = emptyList()
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: CancellationException) {
                // coroutine გაუქმდა — იგნორი OK
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("NOTES", "Exception: ${e.localizedMessage}")
            }
        }
    }

    fun createNote() {
        isLoading.value = true
        generalError.value = null

        viewModelScope.launch {
            try {
                val request = NoteDTO(
                    title = noteTitle.value!!,
                    desc = noteDesc.value!!
                )

                val response = repository.createNewNote(request)
                if (response.isSuccessful) {
                    addNoteSuccess.value = true
                } else {
                    addNoteSuccess.value = false
                    generalError.value = "დამატება ვერ მოხდა"
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("NOTES", "Exception: ${e.localizedMessage}")
            }finally {
                isLoading.value = false
            }
        }
    }

    fun updateNote(id: String) {
        isLoading.value = true
        generalError.value = null

        viewModelScope.launch {
            val request = NoteDTO(
                title = noteTitle.value!!,
                desc = noteDesc.value!!
            )

            try {
                val response = repository.updateExistingNote(id, request)
                if (response.isSuccessful) {
                    updateSuccess.value = true
                } else {
                    updateSuccess.value = false
                    generalError.value = "განახლება ვერ მოხდა"
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("NOTES", "Exception: ${e.localizedMessage}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteNote(id: String) {
        isLoading.value = true
        generalError.value = null

        viewModelScope.launch {
            try {
                val response = repository.deleteExistingNote(id)
                if (response.isSuccessful) {
                    deleteSuccess.value = true
                } else {
                    deleteSuccess.value = false
                    generalError.value = "წაშლა ვერ მოხდა"
                    Log.e("NOTES", "Error Body: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                generalError.value = "პრობლემა, სცადეთ მოგვიანებით"
                Log.e("NOTES", "Exception: $e")
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun parseDate(dateString: String): Date? {
        return try {
            // დასაწყისის ფორმატს ვუთითებთ — 2025-05-15T20:09:59.3599945
            val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS", Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
}