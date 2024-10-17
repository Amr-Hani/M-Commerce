package com.example.mcommerce.ui.authentication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerce.model.firebase.IRepo
import com.example.mcommerce.model.firebase.Repo
import com.example.mcommerce.model.network.Repository

class AuthenticationViewModelFactory(private val repo: IRepo,private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return return if (modelClass.isAssignableFrom(AuthenticationViewModel::class.java)) {
            AuthenticationViewModel(repo,repository) as T
        } else {
            throw IllegalArgumentException("class Authentication view  model is not found!")
        }

    }
}