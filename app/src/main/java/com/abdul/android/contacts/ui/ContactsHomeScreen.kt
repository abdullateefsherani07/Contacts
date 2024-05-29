package com.abdul.android.contacts.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.abdul.android.contacts.R
import com.abdul.android.contacts.entities.MiniContact
import com.abdul.android.contacts.models.MiniContactsViewModel

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsHomeScreen(
    viewModelStoreOwner: ViewModelStoreOwner,
    navigateToDetails: (MiniContact) -> Unit
){

//    val contactsViewModel = ViewModelProvider(viewModelStoreOwner).get(ContactsViewModel::class.java)
//    val labelsViewModel = ViewModelProvider(viewModelStoreOwner).get(LabelsViewModel::class.java)
//
//    val contactsViewState by contactsViewModel.contactsState
//    val labelsViewState by labelsViewModel.labelsState

    val miniContactsViewModel = ViewModelProvider(viewModelStoreOwner).get(MiniContactsViewModel::class.java)
    val miniContactsState by miniContactsViewModel.miniContactsState
    
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    var selectedItem by remember {
        mutableIntStateOf(0)
    }
    val items = listOf(
        BottomNavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavigationItem("Favourites", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder),
        BottomNavigationItem("You", Icons.Filled.AccountCircle, Icons.Filled.AccountCircle)
    )
    Scaffold(
        topBar = {
            if(isPortrait){
                TopAppBar(
                    title = {
                        when (selectedItem) {
                            0 -> Text(text = "Contacts")
                            1 -> Text(text = "Favourites")
                            2 -> Text(text = "Contacts")
                            else -> Text(stringResource(R.string.app_name))
                        }
                    }
                )
            }
        },
        bottomBar = {
            if(isPortrait){
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            label = { Text(item.title) },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItem) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
            }

        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "New Contact")
            }
        }
    ) {
        Row {
            if(!isPortrait){
                NavigationRail(
                    header = {
                        Icon(painter = painterResource(id = R.drawable.google_contacts_seeklogo), contentDescription = "")
                    }
                ) {
                    items.forEachIndexed { index, item ->
                        NavigationRailItem(
                            selected = selectedItem == index,
                            onClick = { selectedItem = index },
                            label = { Text(text = item.title) },
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItem) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        )
                    }
                }
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(it)){
                when{
                    miniContactsState.loading ->{
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    miniContactsState.error != null -> {
                        Text(text = "Error Occurred")
                    }

                    selectedItem == 0 -> {
                        ContactsScreen(contacts = miniContactsState.miniContacts, navigateToDetails)
                    }

                    selectedItem == 1 -> {
                        FavouritesContactsScreen(contacts = miniContactsState.miniContacts.filter { it.isFavourite }, navigateToDetails)
                    }

                    selectedItem == 2 -> {
                        ContactsScreen(contacts = miniContactsState.miniContacts, navigateToDetails)
                    }

                    else -> {
                        ContactsScreen(contacts = miniContactsState.miniContacts, navigateToDetails)
                    }
                }
            }
        }

    }

}