package com.abdul.android.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.models.ContactDetailsViewModel

@Composable
fun ContactDetailsScreen(
    viewModel: ContactDetailsViewModel,
    onBackPressed: () -> Unit,
    toggleFavorite: (Contact) -> Unit
){
    val viewState by viewModel.contactDetailsState
    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.surface)){
        when{
            viewState.loading ->{
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            viewState.error != null -> {
                Text(text = "Error Occurred")
            }

            else -> {
                ContactDetails(contact = viewState.contact, onBackPressed, toggleFavorite)
            }
        }
    }
}