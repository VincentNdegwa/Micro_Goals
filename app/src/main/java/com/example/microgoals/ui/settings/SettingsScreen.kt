package com.example.microgoals.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.microgoals.presentation.sign_in.GoogleAuthUiClient
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    googleAuthClient: GoogleAuthUiClient,
    onLogout: () -> Unit
) {
    val userData = googleAuthClient.getSignedInUser()
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (userData != null) {
            Text("User ID: ${userData.userId}")
            Text("Username: ${userData.username}")
            userData.profilePictureUrl?.let { url ->
                // Display profile picture if available
                // You can use an image loading library like Coil to load the image
            }
        } else {
            Text("No user data available")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            coroutineScope.launch {
                googleAuthClient.signOut()
                onLogout()
            }
        }) {
            Text("Log Out")
        }
    }
}

