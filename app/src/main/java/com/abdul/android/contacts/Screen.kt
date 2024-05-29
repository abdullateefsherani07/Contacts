package com.abdul.android.contacts

sealed class Screen(var route: String) {
    object ContactsHomeScreen: Screen("contactshomescreen")
    object ContactsScreen: Screen("contactsscreen")
    object ContactDetailsScreen: Screen("contactdetailsscreen")
    object ContactDetails: Screen("contactdetails")
    object FavouritesContactsScreen: Screen("favouritescontactsscreen")
}