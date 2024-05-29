package com.abdul.android.contacts.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.abdul.android.contacts.R
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.entities.Email
import com.abdul.android.contacts.entities.MiniContact
import com.abdul.android.contacts.entities.PhoneNumber

@Composable
fun LabelsRow(labels: List<String>){
    LazyRow(modifier = Modifier
//        .fillMaxWidth()
    ) {
        items(labels){label ->
            AssistChip(
                modifier = Modifier.padding(8.dp, 0.dp),
                onClick = { /*TODO*/ },
                label = { Text(text = label) }
            )
        }
    }
}

@Composable
fun FavouriteContactItem(contact: MiniContact, navigateToDetails: (MiniContact) -> Unit){
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor by remember { mutableStateOf(randomColor(isDarkTheme)) }
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { navigateToDetails(contact) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!contact.profilePhoto.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(
                    contact.profilePhoto,
                ),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(70.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(color = backgroundColor)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(70.dp)
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
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            text = contact.name,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            textAlign = TextAlign.Center,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun ContactHeader(contact: Contact, backgroundColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!contact.profilePhoto.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(contact.profilePhoto),
                contentDescription = "Profile Photo",
                modifier = Modifier
                    .size(172.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(color = backgroundColor)
                    .fillMaxWidth()
            )
        } else {
            Box(
                modifier = Modifier
                    .size(172.dp)
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(color = backgroundColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val firstChar = if (contact.name.isNotEmpty()) contact.name.first().toString() else ""
                Text(
                    text = firstChar,
                    style = TextStyle(fontSize = 70.sp),
                    color = Color.White
                )
            }
        }
        Text(
            text = contact.name,
            style = TextStyle(fontSize = 28.sp),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ContactActions(
    contact: Contact,
    context: Context,
    callLauncher: ActivityResultLauncher<String>,
    messageLauncher: ActivityResultLauncher<String>
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ContactActionItem(
            iconResId = R.drawable.oplus_call_filled,
            text = "Call",
            onClick = {
                if(contact.phoneNumbers[0]?.phoneNumber != null){
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                        makeCall(contact.phoneNumbers[0]!!.phoneNumber, context)
                    } else {
                        callLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }

            },
            modifier = Modifier.weight(1f)
        )
        ContactActionItem(
            iconResId = R.drawable.oplus_sms_filled,
            text = "Message",
            onClick = {
                if(contact.phoneNumbers[0]?.phoneNumber != null){
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        sendMessage(contact.phoneNumbers[0]!!.phoneNumber, context)
                    } else {
                        messageLauncher.launch(Manifest.permission.SEND_SMS)
                    }
                }
            },
            modifier = Modifier.weight(1f)
        )
        ContactActionItem(
            iconResId = R.drawable.oplus_video_call_filled,
            text = "Video",
            onClick = {},
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ContactActionItem(iconResId: Int, text: String, onClick: () -> Unit, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .then(modifier)
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = MaterialTheme.colorScheme.primaryContainer
            )
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = text
        )
        Text(
            text = text,
            modifier = Modifier.padding(5.dp),
            style = TextStyle(fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
        )
    }
}

@Composable
fun ContactInfoPhone(numbers: List<PhoneNumber?>, context: Context, callLauncher: ActivityResultLauncher<String>) {
    if(numbers.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            numbers.forEach { phoneNumber ->
                ContactDetailRow(
                    iconResId = R.drawable.oplus_call_filled,
                    primaryText = phoneNumber!!.phoneNumber,
                    secondaryText = phoneNumber.type,
                    onClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                            makeCall(phoneNumber.phoneNumber, context)
                        } else {
                            callLauncher.launch(Manifest.permission.CALL_PHONE)
                        }
                    }
                )
            }
        }
    }
}
@Composable
fun ContactInfoEmail(emails: List<Email?>, context: Context) {
    if(emails.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            emails.forEach {email ->
                ContactDetailRow(
                    iconResId = R.drawable.oplus_email_filled,
                    primaryText = email!!.email,
                    secondaryText = email.type,
                    onClick = { /* Handle click if necessary */ }
                )
            }
        }
    }
}

@Composable
fun ContactInfoWhatsApp(numbers: List<PhoneNumber?>, context: Context) {
    if(numbers.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer
                )
        ) {
            numbers.forEach { number->
                ContactDetailRow(
                    iconResId = R.drawable.ic_whatsapp_outlined,
                    primaryText = "Message ${number!!.phoneNumber}",
                    null,
                    onClick = {
                        val uri = Uri.parse("https://wa.me/${number.phoneNumber}")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun ContactDetailRow(iconResId: Int, primaryText: String, secondaryText: String?, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = "",
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp)
        )
        Column(
            modifier = Modifier.padding(5.dp, 0.dp),
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = primaryText,
                style = TextStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    fontWeight = MaterialTheme.typography.titleMedium.fontWeight,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.padding(2.dp, 0.dp)
            )
            if (secondaryText != null){
                Text(
                    text = secondaryText,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        fontWeight = MaterialTheme.typography.labelMedium.fontWeight,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.padding(2.dp, 0.dp)
                )
            }
        }
    }
}

private fun isAppInstalled(packageName: String, context: Context): Boolean{
    val packageManager = context.packageManager
    return try {
        packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException){
        false
    }
}