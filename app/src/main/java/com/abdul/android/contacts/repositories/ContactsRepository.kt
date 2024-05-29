package com.abdul.android.contacts.repositories

import android.content.ContentResolver
import android.content.ContentValues
import android.provider.ContactsContract
import android.util.Log
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.entities.Email
import com.abdul.android.contacts.entities.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val contentResolver: ContentResolver) {

    // Function to retrieve com.abdul.android.contacts from the device's com.abdul.android.contacts database
    suspend fun getContacts(): List<Contact> {
        return withContext(Dispatchers.IO) {
            try {
                val contactsMap = mutableMapOf<String, Contact>()
                val phoneNumbersSet = mutableSetOf<String>()
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
                        val countryCode = getCountryCode(phoneNumber)
                        val emails: MutableList<Email?> = mutableListOf()
                        val labels = getLabelsForContact(contentResolver, id)

                        // Retrieving emails associated with the contact
                        val emailCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )
                        emailCursor?.use{emailCur ->
                            val emailIndex = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                            while (emailCur.moveToNext()){
                                val email = emailCur.getString(emailIndex)
                                val emailType = getEmailType(contentResolver, email)
                                emails.add(Email(email, emailType))
                            }
                        }

                        // Formatting phone number and adding contact details to the map
                        val formattedPhoneNumber = phoneNumber.replace("[\\s-]".toRegex(), "")
                        if(name.isNotBlank() && formattedPhoneNumber !in phoneNumbersSet){
                            val isFavorite = isContactFavorite(contentResolver, id)
                            val phoneNumberType = getPhoneNumberType(contentResolver, formattedPhoneNumber)
                            val phoneNumberObject = PhoneNumber(phoneNumber, phoneNumberType ?: "Unknown")
                            val contact = contactsMap[id] ?: Contact(id, name, countryCode, mutableListOf(), profilePhoto, emails, mutableListOf(), isFavorite)
                            phoneNumbersSet.add(formattedPhoneNumber)
                            contact.phoneNumbers.add(phoneNumberObject)
                            contact.labels.addAll(labels)
                            contactsMap[id] = contact
                        }
                    }
                }
                contactsMap.values.toList()
            } catch (e: Exception) {
                Log.e("Contacts Repository", "Error while getting com.abdul.android.contacts from database", e)
                emptyList()
            }
        }
    }
}

// Function to retrieve labels for a contact
suspend fun getLabelsForContact(contentResolver: ContentResolver, contactId: String): List<String> {
    return withContext(Dispatchers.IO) {
        try {
            val labels = mutableListOf<String>()

            // Querying labels
            val groupCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID),
                ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?",
                arrayOf(contactId, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE),
                null
            )
            groupCursor?.use {
                val groupIdIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)
                while (it.moveToNext()) {
                    val groupId = it.getString(groupIdIndex)
                    val groupName = getGroupName(contentResolver, groupId)
                    if (groupName.isNotBlank()) {
                        labels.add(groupName)
                    }
                }
            }

            // Querying organizations
            val organizationsCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Organization.COMPANY),
                "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Organization.CONTACT_ID} = ?",
                arrayOf(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE, contactId),
                null
            )
            organizationsCursor?.use {
                val organizationNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
                while (it.moveToNext()) {
                    val organizationName = if(organizationNameIndex >= 0) it.getString(organizationNameIndex) else ""
                    labels.add(organizationName)
                }
            }
            labels
        } catch(e: Exception) {
            Log.e("Contacts Repository", "Error while getting labels for contact from database", e)
            emptyList()
        }
    }
}

// Function to get group name from group ID
fun getGroupName(contentResolver: ContentResolver, groupId: String): String {
    var groupName = ""
    val cursor = contentResolver.query(
        ContactsContract.Groups.CONTENT_URI,
        arrayOf(ContactsContract.Groups.TITLE),
        ContactsContract.Groups._ID + " = ?",
        arrayOf(groupId),
        null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            val groupNameIndex = it.getColumnIndex(ContactsContract.Groups.TITLE)
            groupName = it.getString(groupNameIndex)
        }
    }
    return groupName
}


// Function to retrieve the type of phone number (e.g., Home, Mobile, Work, etc.)
suspend fun getPhoneNumberType(contentResolver: ContentResolver, phoneNumber: String): String? {
    return withContext(Dispatchers.IO) {
        var phoneType: String? = null
        try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Phone.TYPE),
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                arrayOf(phoneNumber),
                null
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val typeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)
                    if (typeIndex != -1) {
                        val type = it.getInt(typeIndex)
                        phoneType = when (type) {
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                            ContactsContract.CommonDataKinds.Phone.TYPE_OTHER -> "Other"
                            else -> "Custom"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Contacts Repository", "Error while retrieving phone number type", e)
        }
        phoneType
    }
}

suspend fun getEmailType(contentResolver: ContentResolver, email: String): String {
    return withContext(Dispatchers.IO) {
        var emailType: String = "Unknown"
        try {
            val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                arrayOf(ContactsContract.CommonDataKinds.Email.TYPE),
                ContactsContract.CommonDataKinds.Email.ADDRESS + " = ?",
                arrayOf(email),
                null
            )
            cursor?.use {
                if(it.moveToFirst()){
                    val emailTypeIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE)
                    if (emailTypeIndex != -1){
                        val type = it.getInt(emailTypeIndex)
                        emailType = when(type){
                            ContactsContract.CommonDataKinds.Email.TYPE_HOME -> "Home"
                            ContactsContract.CommonDataKinds.Email.TYPE_MOBILE -> "Home"
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK -> "Home"
                            ContactsContract.CommonDataKinds.Email.TYPE_OTHER -> "Home"
                            else -> "Custom"
                        }
                    }
                }
            }
        } catch (e: Exception){
            Log.e("Contacts Repository", "Error while retrieving email type", e)
        }
        emailType
    }
}

// Function to mark a contact as favorite
suspend fun markAsFavorite(contentResolver: ContentResolver, contactId: String) {
    Log.e("Contacts Repository", "Start of markAsFavorite function")
    withContext(Dispatchers.IO) {
        Log.e("Contacts Repository", "Coroutine Scope launched")
        try {
            val contentValues = ContentValues().apply {
                put(ContactsContract.Contacts.STARRED, 1) // 1 for marking as favorite, 0 for unmarking
            }
            val selection = ContactsContract.Contacts._ID + " = ?"
            val selectionArgs = arrayOf(contactId)
            contentResolver.update(
                ContactsContract.Contacts.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs
            )
        } catch (e: Exception){
            Log.e("Contacts Repository", "Error while adding as favorite", e)
        }
    }
}

// Function to remove a contact from favorites
suspend fun removeFromFavorites(contentResolver: ContentResolver, contactId: String) {
    Log.e("Contacts Repository", "Start of removeFromFavorites function")
    withContext(Dispatchers.IO) {
        Log.e("Contacts Repository", "Coroutine Scope launched")
        try {
            val contentValues = ContentValues().apply {
                put(ContactsContract.Contacts.STARRED, 0) // 0 for unmarking as favorite
            }
            val selection = ContactsContract.Contacts._ID + " = ?"
            val selectionArgs = arrayOf(contactId)
            contentResolver.update(
                ContactsContract.Contacts.CONTENT_URI,
                contentValues,
                selection,
                selectionArgs
            )
        } catch (e: Exception) {
            Log.e("Contacts Repository", "Error while removing as favorite", e)
        }
    }
}

// Function to check if a contact is marked as favorite
fun isContactFavorite(contentResolver: ContentResolver, contactId: String): Boolean {
    val projection = arrayOf(ContactsContract.Contacts.STARRED)
    val selection = ContactsContract.Contacts._ID + " = ?"
    val selectionArgs = arrayOf(contactId)
    val cursor = contentResolver.query(
        ContactsContract.Contacts.CONTENT_URI,
        projection,
        selection,
        selectionArgs,
        null
    )
    cursor?.use {
        if (it.moveToFirst()) {
            val starredIndex = it.getColumnIndex(ContactsContract.Contacts.STARRED)
            val starred = if(starredIndex >= 0) it.getInt(starredIndex) else 0
            return starred == 1 // 1 means contact is marked as favorite
        }
    }
    return false
}

// Function to extract country code from phone number
fun getCountryCode(phoneNumber: String): String {
    val countryCodeLength = phoneNumber.length - 10

    return if (countryCodeLength > 0){
        phoneNumber.substring(0, countryCodeLength)
    } else {
        ""
    }
}