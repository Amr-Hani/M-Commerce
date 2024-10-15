package com.example.mcommerce.ui.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerce.model.firebase.IRepo
import com.example.mcommerce.model.firebase.Repo

class AuthenticationViewModelFactory(private val repo: IRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return return if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            AuthenticationViewModel(repo) as T
        } else {
            throw IllegalArgumentException("class Authentication view  model is not found!")
        }

    }
}