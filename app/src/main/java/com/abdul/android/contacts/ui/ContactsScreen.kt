package com.abdul.android.contacts.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.abdul.android.contacts.entities.MiniContact

data class Category(
    val name: String,
    val contacts: List<MiniContact>
)
@Composable
private fun CategoryHeader(
    text: String,
    modifier: Modifier = Modifier
){
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    )
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsScreen(contacts: List<MiniContact>, navigateToDetails: (MiniContact) -> Unit){
    val categorizedList = contacts.groupBy { it.name.first() }.toSortedMap()
    val categories = categorizedList.map { 
        Category(
            name = it.key.toString(),
            contacts = it.value
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            categories.forEach { category ->
                stickyHeader {
                    CategoryHeader(text = category.name)
                }
                items(category.contacts){ contact ->
                    ContactItem(contact = contact, navigateToDetails = navigateToDetails)
                }
            }
        }
    }
}

@Composable
fun ContactItem(contact: MiniContact, navigateToDetails: (MiniContact) -> Unit){
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor by remember { mutableStateOf(randomColor(isDarkTheme)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp, 3.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                navigateToDetails(contact)
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        if (!contact.profilePhoto.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    contact.profilePhoto,
                ),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(color = backgroundColor)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .background(color = backgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.name.first().toString(),
                    style = TextStyle(fontSize = 24.sp),
                    color = Color.White
                )
            }
        }
        Text(
            text = contact.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

fun randomColor(isDarkTheme: Boolean): Color {
    val colorsLight = listOf(
        Color(0xFF0AD270),
        Color(0xFFF02867),
        Color(0xFFFF9623),
        Color(0xFF6442B3),
        Color(0xFFFFC700),
        Color(0xFFC41442),
        Color(0xFF23BFD6)
    )

    val colorsDark = listOf(
        Color(0xFF067C51),
        Color(0xFFA01C45),
        Color(0xFFCC781D),
        Color(0xFF3A2577),
        Color(0xFFB39E00),
        Color(0xFF7A0F2F),
        Color(0xFF178C9A)
    )

    return if(isDarkTheme){
        colorsDark.random()
    } else {
        colorsLight.random()
    }

}