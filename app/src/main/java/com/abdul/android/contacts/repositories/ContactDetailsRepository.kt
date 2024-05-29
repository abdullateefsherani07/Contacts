package com.abdul.android.contacts.repositories

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.entities.Email
import com.abdul.android.contacts.entities.PhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactDetailsRepository(private val contentResolver: ContentResolver) {

    suspend fun getContactDetails(contactId: String): Contact? {
        return withContext(Dispatchers.IO) {
            try {
                var contact: Contact? = null
                var phoneNumbersSet: MutableList<String> = mutableListOf()
                val cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(contactId),
                    null
                )
                cursor?.use {
                    val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    val profilePhotoIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                    val countryCode = getCountryCode(contactId)
                    val emails: MutableList<Email?> = mutableListOf()
                    val numbers: MutableList<PhoneNumber?> = mutableListOf()
                    val labels = getLabelsForContact(contentResolver, contactId)

                    // Retrieving emails associated with the contact
                    val emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )
                    emailCursor?.use { emailCur ->
                        val emailIndex = emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
                        while (emailCur.moveToNext()) {
                            val email = emailCur.getString(emailIndex)
                            val emailType = getEmailType(contentResolver, email)
                            emails.add(Email(email, emailType))
                        }
                    }

                    val numberCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(contactId),
                        null
                    )

                    numberCursor?.use { numberCur ->
                        val numberIndex = numberCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA)
                        while (numberCur.moveToNext()) {
                            val number = numberCur.getString(numberIndex)
                            val numberType = getPhoneNumberType(contentResolver, number)
                            val formattedNumber = number.replace("[\\s-]".toRegex(), "")
                            if (formattedNumber !in phoneNumbersSet) {
                                numbers.add(PhoneNumber(formattedNumber, numberType ?: "Unknown"))
                                phoneNumbersSet.add(formattedNumber)
                            }

                        }
                    }

                    if (it.moveToFirst()) {
                        val name = if (nameIndex >= 0) it.getString(nameIndex) else ""
                        val profilePhoto = if (profilePhotoIndex >= 0) it.getString(profilePhotoIndex) else ""
                        val isFavorite = isContactFavorite(contentResolver, contactId)
                        contact = Contact(contactId, name, countryCode, numbers, profilePhoto, emails, mutableListOf(), isFavorite)
                        contact?.labels?.addAll(labels)
                    }
                }
                contact
            } catch (e: Exception) {
                Log.e("Contacts Repository", "Error while getting contact from database", e)
                null
            }
        }
    }

}