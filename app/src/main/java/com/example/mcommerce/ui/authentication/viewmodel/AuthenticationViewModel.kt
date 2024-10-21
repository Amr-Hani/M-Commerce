package com.example.mcommerce.ui.authentication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.firebase.IRepo
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.UpdateCustomerRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.CustomerResponse
import com.example.mcommerce.model.responses.CustomersByEmailResponse
import com.example.mcommerce.model.responses.ReceivedDraftOrder
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
    private  val TAG = "AuthenticationViewModel"
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
            try {
                val response = repository.postCustomer(customer)
                .catch {
                    mutableCustomerResponseStateFlow.value = ApiState.Failure(it.toString())
                    Log.e("TAG", "postCustomer: ${it.message}")
                }
                .collectLatest {
                    mutableCustomerResponseStateFlow.value = ApiState.Success(it)
                }
                // Handle the response
            } catch (e: Exception) {
                Log.d("TAG", "postCustomer: ${e.printStackTrace()}")
            }

        }
    }

    private val mutableCustomerEmailResponseStateFlow =
        MutableStateFlow<ApiState<CustomersByEmailResponse>>(ApiState.Loading())
    val customerEmailResponseDetailsStateFlow = mutableCustomerEmailResponseStateFlow.asStateFlow()


    fun getCustomerByEmail(customerEmail:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getCustomerByEmail(customerEmail)
                .catch {
                    mutableCustomerEmailResponseStateFlow.value = ApiState.Failure(it.toString())
                    Log.e("TAG", "postCustomer: ${it.message}")
                }
                .collectLatest {
                    mutableCustomerEmailResponseStateFlow.value = ApiState.Success(it)
                }
                // Handle the response
            } catch (e: Exception) {
                Log.d("TAG", "postCustomer: ${e.printStackTrace()}")
            }

        }
    }

    private val mutableCreatedOrderStateFlow =
        MutableStateFlow<ApiState<ReceivedDraftOrder>>(ApiState.Loading())
    val createdOrderStateFlow = mutableCreatedOrderStateFlow.asStateFlow()

    private val mutableUpdateCustomerStateFlow =
        MutableStateFlow<ApiState<CustomerResponse>>(ApiState.Loading())
    val updateCustomerStateFlow = mutableUpdateCustomerStateFlow.asStateFlow()

    private val mutableUpdatedOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    private val mutableAllDraftOrdersStateFlow =
        MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val allDraftOrdersStateFlow = mutableAllDraftOrdersStateFlow.asStateFlow()

    private val mutableCardDraftOrdersStateFlow =
        MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val cardDraftOrdersStateFlow = mutableCardDraftOrdersStateFlow.asStateFlow()

    private val mutableDraftOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()


    private val mutableGetCustomerBuIdStateFlow =
        MutableStateFlow<ApiState<CustomerResponse>>(ApiState.Loading())
    val getCustomerByIdStateFlow = mutableGetCustomerBuIdStateFlow.asStateFlow()

    fun createFavoriteDraftOrder(draftOrderRequest: DraftOrderRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.createFavoriteDraftOrder(draftOrderRequest).catch {
                mutableCreatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "there was error while creating error: ${it.message}")
            }.collect {
                mutableCreatedOrderStateFlow.value = ApiState.Success(it)
                Log.d(TAG, "createOrder: ${it.id}")
                Log.i(TAG, "created the Order successfully")
            }
        }
    }

    fun getAllFavoriteDraftOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavoriteDraftOrders().catch {
                mutableAllDraftOrdersStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableAllDraftOrdersStateFlow.value = ApiState.Success(it.draft_orders)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }

    fun getCardDraftOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllFavoriteDraftOrders().catch {
                mutableCardDraftOrdersStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableCardDraftOrdersStateFlow.value = ApiState.Success(it.draft_orders)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }






    fun getFavoriteDraftOrder(draftOrderId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavoriteDraftOrder(draftOrderId).catch {
                mutableDraftOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableDraftOrderStateFlow.value = ApiState.Success(it)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }

    fun updateCustomerById(customerId: Long,updateCustomerRequest: UpdateCustomerRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCustomerById(customerId,updateCustomerRequest).catch {
                mutableUpdateCustomerStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableUpdateCustomerStateFlow.value = ApiState.Success(it)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }

    fun getCustomerById(customerId: Long)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerById(customerId).catch {
                mutableGetCustomerBuIdStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableGetCustomerBuIdStateFlow.value = ApiState.Success(it)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }

    fun updateFavoriteDraftOrder(
        customerId: Long,
        updateDraftOrderRequest: UpdateDraftOrderRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest).catch {
                mutableUpdatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error updateDraftOrder: ${it.message}")
            }.collect {
                mutableUpdatedOrderStateFlow.value = ApiState.Success(it.draft_order)
                Log.i(TAG, "updateDraftOrder: $it")
            }
        }
    }

}


