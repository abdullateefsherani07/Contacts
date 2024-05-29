package com.abdul.android.contacts.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MiniContact(
    var id: String,
    var name: String,
    var profilePhoto: String?,
    var isFavourite: Boolean = false
): Parcelable