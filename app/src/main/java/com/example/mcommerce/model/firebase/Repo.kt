package com.example.mcommerce.model.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class Repo(private val fireBaseDataSource: IFireBaseDataSource) : IRepo {
    companion object {
        private var instance: Repo? = null
        fun getInstance(
            fireBaseDataSource: FireBaseDataSource
        ): Repo {
            return instance ?: synchronized(this) {
                val temp = Repo(fireBaseDataSource)
                instance = temp
                temp
            }
        }
    }

    //SignUp
    override fun signUp(email: String, password: String): Task<AuthResult> {
        return fireBaseDataSource.signUp(email, password)
    }

    override fun sendVerificationEmail(user: FirebaseUser?): Task<Void>? {
        return fireBaseDataSource.sendVerificationEmail(user)
    }
    //LogIn
    override fun logIn(email: String, password: String): Task<AuthResult> {
        return fireBaseDataSource.logIn(email, password)
    }
    override fun checkIfEmailVerified(): FirebaseUser? {
        return fireBaseDataSource.checkIfEmailVerified()
    }

}