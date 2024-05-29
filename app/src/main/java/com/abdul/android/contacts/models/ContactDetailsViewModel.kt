package com.abdul.android.contacts.models

import android.app.Application
import android.content.ContentResolver
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.repositories.ContactDetailsRepository
import com.abdul.android.contacts.repositories.markAsFavorite
import com.abdul.android.contacts.repositories.removeFromFavorites
import kotlinx.coroutines.launch

class ContactDetailsViewModel(application: Application): AndroidViewModel(application) {

    private val contentResolver: ContentResolver = application.contentResolver

    private val _contactDetailsState = mutableStateOf(ContactDetailsState())
    val contactDetailsState: State<ContactDetailsState> = _contactDetailsState

    private val repository = ContactDetailsRepository(contentResolver)
    var contact by mutableStateOf<Contact?>(null)
        private set

    private fun loadContactDetails(contactId: String){
        viewModelScope.launch {
            try{
                contact = repository.getContactDetails(contactId = contactId)
                _contactDetailsState.value = _contactDetailsState.value.copy(
                    contact = contact,
                    loading = false,
                    error = null
                )
            } catch(e:Exception){
                _contactDetailsState.value = _contactDetailsState.value.copy(
                    loading = false,
                    error = "Error fetching com.abdul.android.contacts ${e.message}"
                )
            }
        }
    }

    fun fetchContactDetails(contactId: String) {
        loadContactDetails(contactId)
    }

    fun updateState(){
        _contactDetailsState.value = _contactDetailsState.value.copy(
            loading = true,
            contact = null,
            error = null
        )
    }

    fun toggleFavorite(contact: Contact){
        Log.e("ViewModel", "Start of toggleFavorite function")
        viewModelScope.launch {
            Log.e("ViewModel", "ViewModel Scope Launched")
            if (contact.isFavourite){
                removeFromFavorites(contentResolver, contact.id)
                contact.isFavourite = false
                Log.e("ViewModel", "isFavourite for this contact se to false")
            } else {
                markAsFavorite(contentResolver, contact.id)
                contact.isFavourite = true
                Log.e("ViewModel", "isFavourite for this contact se to true")
            }
        }
    }

    data class ContactDetailsState(
        val loading: Boolean = true,
        var contact: Contact? = null,
        val error: String? = null
    )
}