package com.abdul.android.contacts

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.abdul.android.contacts.entities.Contact
import com.abdul.android.contacts.models.ContactDetailsViewModel
import com.abdul.android.contacts.ui.ContactDetailsScreen
import com.abdul.android.contacts.ui.ContactDetails
import com.abdul.android.contacts.ui.ContactsHomeScreen
import kotlinx.coroutines.launch

@Composable
fun ContactsApp(
    navController: NavHostController,
    viewModelStoreOwner: ViewModelStoreOwner
) {
    val contactDetailsViewModel = ViewModelProvider(viewModelStoreOwner).get(ContactDetailsViewModel::class.java)
    val contactDetailsViewState by contactDetailsViewModel.contactDetailsState
    val currentRoute = remember {
        mutableStateOf(Screen.ContactsHomeScreen.route)
    }

    NavHost(
        navController = navController,
        startDestination = Screen.ContactsHomeScreen.route
    ) {
        composable(route = Screen.ContactsHomeScreen.route) {
            currentRoute.value = Screen.ContactsHomeScreen.route
            Log.e("Navigation", "Current Route: ${currentRoute.value}")
            ContactsHomeScreen(
                viewModelStoreOwner = viewModelStoreOwner,
                navigateToDetails = {
                    navController.currentBackStackEntry?.savedStateHandle?.set("contact", it)
                    contactDetailsViewModel.viewModelScope.launch {
                        contactDetailsViewModel.fetchContactDetails(it.id)
                    }
                    navController.navigate(Screen.ContactDetails.route)
                }
            )
        }
        composable(
            route = Screen.ContactDetailsScreen.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            val contact = navController.previousBackStackEntry?.savedStateHandle?.
            get<Contact>("contact") ?: Contact("", "", "", mutableListOf(), null, mutableListOf())

            currentRoute.value = Screen.ContactDetailsScreen.route
            Log.e("Navigation", "Current Route: ${currentRoute.value}")

            ContactDetails(
                contact = contact,
                onBackPressed = {
                    navController.navigateUp()
                },
                toggleFavorite = {
                    Log.e("App", "Calling toggleFavorite function")
                }
            )
        }

        composable(
            route = Screen.ContactDetails.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideIntoContainer(
                    animationSpec = tween(300, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(300, easing = LinearEasing)
                ) + slideOutOfContainer(
                    animationSpec = tween(300, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ){

            currentRoute.value = Screen.ContactDetails.route
            Log.e("Navigation", "Current Route: ${currentRoute.value}")

            ContactDetailsScreen(
                viewModel = contactDetailsViewModel,
                onBackPressed = {
                    navController.navigateUp()
                    contactDetailsViewModel.updateState()
                },
                toggleFavorite = {
                    contactDetailsViewModel.toggleFavorite(it)
                }
            )
        }
    }
}
