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
import com.abdul.android.contacts.entities.MiniContact
import com.abdul.android.contacts.repositories.MiniContactsRepository
import com.abdul.android.contacts.repositories.markAsFavorite
import com.abdul.android.contacts.repositories.removeFromFavorites
import kotlinx.coroutines.launch

class MiniContactsViewModel(application: Application): AndroidViewModel(application) {

    private val contentResolver: ContentResolver = application.contentResolver

    private val _miniContactsState = mutableStateOf(MiniContactsState())
    val miniContactsState: State<MiniContactsState> = _miniContactsState

    private val repository = MiniContactsRepository(contentResolver)
    var miniContacts by mutableStateOf<List<MiniContact>>(emptyList())
        private set

    init {
        loadMiniContacts()
    }

    private fun loadMiniContacts(){
        viewModelScope.launch {
            try{
                miniContacts = repository.getMiniContacts()
                _miniContactsState.value = _miniContactsState.value.copy(
                    miniContacts = miniContacts,
                    loading = false,
                    error = null
                )
            } catch(e:Exception){
                _miniContactsState.value = _miniContactsState.value.copy(
                    loading = false,
                    error = "Error fetching com.abdul.android.contacts ${e.message}"
                )
            }
        }
    }

    fun fetchMiniContacts() {
        loadMiniContacts()
    }

    fun toggleFavorite(miniContact: MiniContact){
        Log.e("ViewModel", "Start of toggleFavorite function")
        viewModelScope.launch {
            Log.e("ViewModel", "ViewModel Scope Launched")
            if (miniContact.isFavourite){
                removeFromFavorites(contentResolver, miniContact.id)
                miniContact.isFavourite = false
                Log.e("ViewModel", "isFavourite for this contact se to false")
            } else {
                markAsFavorite(contentResolver, miniContact.id)
                miniContact.isFavourite = true
                Log.e("ViewModel", "isFavourite for this contact se to true")
            }
        }
    }

    data class MiniContactsState(
        val loading: Boolean = true,
        var miniContacts: List<MiniContact> = emptyList(),
        val error: String? = null
    )
}