package com.nkr.fashionita.ui.Item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nkr.fashionita.common.BaseViewModel
import com.nkr.fashionita.model.Product

import com.nkr.fashionita.repository.IProductRepository
import com.nkr.fashionita.ui.Item.notedetail.NoteDetailEvent
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext



class NoteViewModel(
    val productRepo: IProductRepository,
    uiContext: CoroutineContext
) : BaseViewModel<NoteDetailEvent>(uiContext) {



    private val noteState = MutableLiveData<Product>()
    val note: LiveData<Product> get() = noteState

    private val deletedState = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> get() = deletedState

    private val updatedState = MutableLiveData<Boolean>()
    val updated: LiveData<Boolean> get() = updatedState

    override fun handleEvent(event: NoteDetailEvent) {
        when (event) {
          //  is NoteDetailEvent.OnStart -> getNote(event.noteId)
           // is NoteDetailEvent.OnDeleteClick -> onDelete()
           // is NoteDetailEvent.OnDoneClick -> updateNote(event.contents)
        }
    }


/*
    private fun onDelete() = launch {
        val deleteResult = noteRepo.deleteNote(note.value!!)

        when (deleteResult) {
            is Result.Value -> deletedState.value = true
            is Result.Error -> deletedState.value = false
        }
    }
    private fun updateNote(contents: String) = launch {
        val updateResult = noteRepo.updateNote(
            note.value!!.copy(description = contents)
        )

        when (updateResult) {
            is Result.Value -> updatedState.value = true
            is Result.Error -> updatedState.value = false
        }
    }
    private fun getNote(noteId: String) = launch {
        if (noteId == "") newNote()
        else {
            val noteResult = noteRepo.getNoteById(noteId)

            when (noteResult) {
                is Result.Value -> noteState.value = noteResult.value
                is Result.Error -> errorState.value = GET_NOTE_ERROR
            }
        }
    }

    */

    private fun newNote() {
        //noteState.value = Product("",getCalendarTime(), "", 0, "rocket_loop", null,"prod_name","prod_price")
    }


    private fun getCalendarTime(): String {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        val format = SimpleDateFormat("d MMM yyyy HH:mm:ss Z")
        format.timeZone = cal.timeZone
        return format.format(cal.time)
    }


}