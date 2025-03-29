package com.example.microgoals.ui.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.microgoals.presentation.sign_in.GoogleAuthUiClient
import com.example.microgoals.presentation.sign_in.UserData
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.identity.Identity


@Composable
fun AuthLoginScreen(
    onLoginSuccess: (data: UserData) -> Unit
) {
    val context = LocalContext.current
    val googleAuthClient = remember { GoogleAuthUiClient(context, Identity.getSignInClient(context)) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            coroutineScope.launch {
                val signInResult = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                if (signInResult.data != null) {
                    onLoginSuccess(signInResult.data)
                } else {
                    // Handle sign-in error
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to Micro Goals")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            coroutineScope.launch {
                val intentSender = googleAuthClient.signIn()
                if (intentSender != null) {
                    launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                } else {
                    // Handle error in getting intent sender
                }
            }
        }) {
            Text("Sign in with Google")
        }
    }
}



