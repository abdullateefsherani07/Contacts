package com.abdul.android.contacts.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.entities.sampleContact
import com.abdul.android.contacts.ui.randomColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetails(
    contact: Contact?,
    onBackPressed: () -> Unit,
    toggleFavorite: (Contact) -> Unit,
){

    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    val displayedContact by remember {
        mutableStateOf(contact)
    }

    val favorite = remember {
        mutableStateOf(
            if(contact?.isFavourite!!){
                Icons.Filled.Favorite
            } else {
                Icons.Outlined.FavoriteBorder
            }
        )
    }

    // Access the back pressed dispatcher
    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current
    val onBackPressedCallback = remember {
        // Create a callback to handle the back button press
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Call the provided onBackPressed function
                onBackPressed()
            }
        }
    }

    // Effect to add and remove the back pressed callback
    DisposableEffect(backPressedDispatcher) {
        backPressedDispatcher!!.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        onDispose {
            onBackPressedCallback.remove()
        }
    }

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor by remember { mutableStateOf(randomColor(isDarkTheme)) }

    val context = LocalContext.current
    val callLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if(isGranted){
//            makeCall(displayedContact.phoneNumber, context)
        }
    }
    val messageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if (isGranted){
//            sendMessage(displayedContact.phoneNumber, context)
        }

    }
    val writeContactsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted ->
        if(isGranted){
            Log.e("ContactDetailsScreen", "Write Contacts Permission granted")
        }
    }

    if (contact != null){
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Go Back")
                        }
                    },
                    actions = {
                        if(contact.isFavourite){
                            IconButton(
                                onClick = {
                                    writeContactsLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                                    favorite.value = Icons.Outlined.FavoriteBorder
                                    toggleFavorite(contact)
                                }
                            ) {
                                Icon(imageVector = favorite.value, contentDescription = "Remove from favourites")
                            }
                        } else if(!contact.isFavourite){
                            IconButton(onClick = {
                                writeContactsLauncher.launch(Manifest.permission.WRITE_CONTACTS)
                                favorite.value = Icons.Filled.Favorite
                                toggleFavorite(contact)
                            }
                            ) {
                                Icon(imageVector = favorite.value, contentDescription = "Add to favourites")
                            }
                        }

                    }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            if(!isPortrait){
                Row(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(it)
                ){
                   LazyColumn(modifier = Modifier.weight(1f)) {
                       item{
                           ContactHeader(contact = displayedContact!!, backgroundColor = backgroundColor)
                       }
                   }
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item{
                            LabelsRow(labels = displayedContact!!.labels)
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactActions(contact = displayedContact!!, context = context, callLauncher = callLauncher, messageLauncher = messageLauncher)
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactInfoPhone(numbers = displayedContact!!.phoneNumbers, context = context, callLauncher = callLauncher)
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactInfoEmail(emails = displayedContact!!.email, context = context)
                            Spacer(modifier = Modifier.height(12.dp))
                            ContactInfoWhatsApp(numbers = displayedContact!!.phoneNumbers, context = context)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp, 0.dp)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surface)
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    item{
                        ContactHeader(contact = displayedContact!!, backgroundColor = backgroundColor)
                        Spacer(modifier = Modifier.height(12.dp))
                        LabelsRow(labels = displayedContact!!.labels)
                        Spacer(modifier = Modifier.height(12.dp))
                        ContactActions(contact = displayedContact!!, context = context, callLauncher = callLauncher, messageLauncher = messageLauncher)
                        Spacer(modifier = Modifier.height(12.dp))
                        ContactInfoPhone(numbers = displayedContact!!.phoneNumbers, context = context, callLauncher = callLauncher)
                        Spacer(modifier = Modifier.height(12.dp))
                        ContactInfoEmail(emails = displayedContact!!.email, context = context)
                        Spacer(modifier = Modifier.height(12.dp))
                        ContactInfoWhatsApp(numbers = displayedContact!!.phoneNumbers, context = context)
                    }
                }
            }

        }
    }
}

fun makeCall(phoneNumber: String, context: Context){
    val intent = Intent(Intent.ACTION_CALL)
    intent.data = Uri.parse("tel: $phoneNumber")
    context.startActivity(intent)
}

fun sendMessage(phoneNumber: String, context: Context){
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("smsto: $phoneNumber")
    context.startActivity(intent)
}

@Preview(showBackground = true)
@Composable
fun ContactDetailsPreview() {
    ContactDetails(
        contact = sampleContact,
        onBackPressed = { /*TODO*/ },
        toggleFavorite = { /*TODO*/ }
    )
}