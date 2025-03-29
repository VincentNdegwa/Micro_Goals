package com.example.microgoals.ui.auth

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.microgoals.R
import com.example.microgoals.data.local.GoalDatabase
import com.example.microgoals.presentation.sign_in.GoogleAuthUiClient
import com.example.microgoals.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun AuthLoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val database = GoalDatabase.getDatabase(context)
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModel.Factory(userDao = database.userDao())
    )
    val googleAuthClient = remember { GoogleAuthUiClient(context, Identity.getSignInClient(context), userViewModel) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            coroutineScope.launch {
                val signInResult = googleAuthClient.signInWithIntent(result.data ?: return@launch)
                if (signInResult.data != null) {
                    onLoginSuccess()
                } else {
                    isLoading = false
                    // Handle sign-in error
                }
            }
        } else {
            isLoading = false
        }
    }

    // Animation for button scaling
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                    onClick = {
                        isLoading = true
                        coroutineScope.launch {
                            val intentSender = googleAuthClient.signIn()
                            if (intentSender != null) {
                                launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                            } else {
                                isLoading = false
                                // Handle error in getting intent sender
                            }
                        }
                    },
                    modifier = Modifier
                        //.scale(scale.value)
                        .padding(horizontal = 32.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF2193b0)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.google_logo),
                            contentDescription = "Google Logo",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign in with Google", fontWeight = FontWeight.Bold)
                        if(isLoading){
                            Spacer(modifier = Modifier.width(8.dp))
                            CircularProgressIndicator(modifier = Modifier.width(20.dp).height(20.dp))
                        }
                    }
                }
        }
    }
}



