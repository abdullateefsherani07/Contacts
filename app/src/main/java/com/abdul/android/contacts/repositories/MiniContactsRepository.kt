package com.abdul.android.contacts.repositories

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.abdul.android.contacts.entities.MiniContact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MiniContactsRepository(private val contentResolver: ContentResolver) {

    suspend fun getMiniContacts(): List<MiniContact> {
        return withContext(Dispatchers.IO) {
            try {
                val miniContactsMap = mutableMapOf<String, MiniContact>()
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                cursor?.use {
                    val idIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                    val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    val profilePhotoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    while (it.moveToNext()) {
                        val id = if (idIndex >= 0) it.getString(idIndex) else ""
                        val name = if (nameIndex >= 0) it.getString(nameIndex) else ""
                        val phoneNumber = if (phoneIndex >= 0) it.getString(phoneIndex) else ""
                        val profilePhoto = if (profilePhotoIndex >= 0) it.getString(profilePhotoIndex) else ""
                        val isFavorite = isContactFavorite(contentResolver, id)
                        val contact = miniContactsMap[id] ?: MiniContact(id, name, profilePhoto, isFavorite)
                        miniContactsMap[id] = contact
                    }
                }
                miniContactsMap.values.toList()
            } catch (e: Exception) {
                Log.e("Mini Contacts Repository", "Error while getting com.abdul.android.contacts from database", e)
                emptyList()
            }
        }
    }
}