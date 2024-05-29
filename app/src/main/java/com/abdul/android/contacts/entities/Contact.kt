package com.abdul.android.contacts.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Contact(
    var id: String,
    var name: String,
    var countryCode: String,
    var phoneNumbers: MutableList<PhoneNumber?> = mutableListOf(),
    var profilePhoto: String?,
    var email: MutableList<Email?> = mutableListOf(),
    var labels: MutableList<String> = mutableListOf(),
    var isFavourite: Boolean = false
): Parcelable

@Parcelize
data class PhoneNumber(
    var phoneNumber: String,
    var type: String
): Parcelable

@Parcelize
data class Email(
    var email: String,
    var type: String
): Parcelable

@Parcelize
data class CallLogEntity(
    var name: String?,
    var number: String?,
    var time: String,
    var duration: String,
    var type: String
):Parcelable

val sampleContact = Contact(
    id = "1",
    name = "Abdul Lateef",
    countryCode = "91",
    phoneNumbers = mutableListOf(PhoneNumber("7231023097", "Work")),
    profilePhoto = "",
    email = mutableListOf(Email("abdullateefsherani30@gmail.com", "Work")),
    labels = mutableListOf("Family Group", "Favorites"),
    isFavourite = true
)