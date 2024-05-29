package com.abdul.android.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.entities.MiniContact
import com.abdul.android.contacts.models.MiniContactsViewModel
import com.abdul.android.contacts.ui.theme.ContactsTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

//    private val contactsViewModel: ContactsViewModel by viewModels{
//        ContactsViewModelFactory(application)
//    }
//
//    private val labelsViewModel: LabelsViewModel by viewModels{
//        LabelsViewModelFactory(application)
//    }

    private val PERMISSIONS_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestContactsPermission()
        setContent {
            val navController = rememberNavController()
            ContactsTheme {
                ContactsApp(navController = navController, this)
            }
        }
    }

    private fun requestContactsPermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE
        )
        if (!arePermissionsGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //TODO: write code that should execute as soon as the permission READ_CONTACTS is granted
            } else {
                finish()
            }
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

}

object Global{
    var recentContacts = mutableListOf<MiniContact>()
}