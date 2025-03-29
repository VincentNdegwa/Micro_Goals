package com.example.microgoals.presentation.sign_in

import com.example.microgoals.data.model.User

data class SignInResult(
    val data: User?,
    val errorMessage: String?
)

