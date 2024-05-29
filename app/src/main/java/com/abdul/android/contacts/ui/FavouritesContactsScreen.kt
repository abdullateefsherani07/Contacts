package com.abdul.android.contacts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.abdul.android.contacts.entities.MiniContact

@Composable
fun FavouritesContactsScreen(contacts: List<MiniContact>, navigateToDetails: (MiniContact) -> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
        ) {
            items(contacts){ contact ->
                FavouriteContactItem(contact = contact, navigateToDetails = navigateToDetails)
            }
        }
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            items(contacts){item ->
//                if(item.isFavourite){
//                    ContactItem(contact = item, navigateToDetails)
//                }
//            }
//        }
    }
}