package com.abdul.android.contacts.repositories

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LabelsRepository(val contentResolver: ContentResolver){

    suspend fun getLabels(): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                var labels = mutableListOf<String>()

                // Querying labels
                val groupsCursor = contentResolver.query(
                    ContactsContract.Groups.CONTENT_URI,
                    arrayOf(ContactsContract.Groups.TITLE),
                    null,
                    null,
                    null
                )
                groupsCursor?.use {
                    val groupNameIndex = it.getColumnIndex(ContactsContract.Groups.TITLE)
                    while (it.moveToNext()) {
                        val groupName = if(groupNameIndex >= 0) it.getString(groupNameIndex) else ""
                        labels.add(groupName)
                    }
                }

                // Querying organizations
                val organizationsCursor = contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Organization.COMPANY),
                    "${ContactsContract.Data.MIMETYPE} = ?",
                    arrayOf(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE),
                    null
                )
                organizationsCursor?.use {
                    val organizationNameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY)
                    while (it.moveToNext()) {
                        val organizationName = if(organizationNameIndex >= 0) it.getString(organizationNameIndex) else ""
                        labels.add(organizationName)
                    }
                }
                labels = labels.distinct().toMutableList()
                for (label in labels) {
                    Log.e("Labels Repository", "Label: $label")
                }
                labels
            } catch(e: Exception) {
                Log.e("Labels Repository", "Error while getting labels from database", e)
                emptyList()
            }
        }
    }
}