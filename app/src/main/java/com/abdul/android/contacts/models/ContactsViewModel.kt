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
import com.abdul.android.contacts.repositories.ContactsRepository
import com.abdul.android.contacts.repositories.markAsFavorite
import com.abdul.android.contacts.repositories.removeFromFavorites
import kotlinx.coroutines.launch

class ContactsViewModel(application: Application): AndroidViewModel(application) {

    private val contentResolver: ContentResolver = application.contentResolver

    private val _contactsState = mutableStateOf(ContactsState())
    val contactsState: State<ContactsState> = _contactsState

    private val repository = ContactsRepository(application.contentResolver)
    var contacts by mutableStateOf<List<Contact>>(emptyList())
        private set

    init {
        loadContacts()
    }

    private fun loadContacts(){
        viewModelScope.launch {
            try{
                contacts = repository.getContacts()
                _contactsState.value = _contactsState.value.copy(
                    contactsList = contacts,
                    loading = false,
                    error = null
                )
            } catch(e:Exception){
                _contactsState.value = _contactsState.value.copy(
                    loading = false,
                    error = "Error fetching com.abdul.android.contacts ${e.message}"
                )
            }
        }
    }

    fun fetchContacts() {
        loadContacts()
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

    data class ContactsState(
        val loading: Boolean = true,
        var contactsList: List<Contact> = emptyList(),
        val error: String? = null
    )
}