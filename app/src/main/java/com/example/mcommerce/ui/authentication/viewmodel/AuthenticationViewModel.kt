package com.example.mcommerce.ui.authentication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mcommerce.model.firebase.IRepo
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel(private val repo: IRepo) : ViewModel() {
    // Sign Up
    fun signUp(email: String, password: String): Task<AuthResult> {
        return repo.signUp(email, password)
    }

    fun sendVerificationEmail(user: FirebaseUser?): Task<Void>? {
        return repo.sendVerificationEmail(user)
    }

    // LogIn
    fun logIn(email: String, password: String): Task<AuthResult> {
        return repo.logIn(email, password)
    }

    fun checkIfEmailVerified(): FirebaseUser? {
        return repo.checkIfEmailVerified()
    }
}