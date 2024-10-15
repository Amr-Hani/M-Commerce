package com.example.mcommerce.model.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface IRepo {
    //SignUp
    fun signUp(email: String, password: String): Task<AuthResult>
    fun sendVerificationEmail(user: FirebaseUser?): Task<Void>?

    //LogIn
    fun logIn(email: String, password: String): Task<AuthResult>
    fun checkIfEmailVerified(): FirebaseUser?
}