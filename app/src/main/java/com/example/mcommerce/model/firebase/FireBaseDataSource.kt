package com.example.mcommerce.model.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class FireBaseDataSource(private val mAuth: FirebaseAuth) : IFireBaseDataSource {
    //SignUp
    override fun signUp(email: String, password: String): Task<AuthResult> {
        return mAuth.createUserWithEmailAndPassword(email, password)
    }

    override fun sendVerificationEmail(user: FirebaseUser?): Task<Void>? {
        return user?.sendEmailVerification()
    }

    //LogIn
    override fun logIn(email: String, password: String): Task<AuthResult> {
        return mAuth.signInWithEmailAndPassword(email, password)
    }

    override fun checkIfEmailVerified(): FirebaseUser? {
        return mAuth.currentUser
    }
}