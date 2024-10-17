package com.example.mcommerce.ui.authentication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.firebase.IRepo
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AuthenticationViewModel(private val repo: IRepo, private val repository: Repository) :
    ViewModel() {
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

    private val mutableCustomerResponseStateFlow =
        MutableStateFlow<ApiState<CustomerResponse>>(ApiState.Loading())
    val customerResponseDetailsStateFlow = mutableCustomerResponseStateFlow.asStateFlow()

    fun postCustomer(customer: CustomerRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.postCustomer(customer)
                .catch {
                    mutableCustomerResponseStateFlow.value = ApiState.Failure(it.toString())
                    Log.e("TAG", "getProductDetailsViewModel: ${it.message}")
                }
                .collectLatest {
                    mutableCustomerResponseStateFlow.value = ApiState.Success(it)
                }
        }
    }
}


