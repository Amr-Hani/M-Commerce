
package com.example.mcommerce.ui.product_info.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository

import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ReceivedDraftOrder

import com.example.mcommerce.model.pojos.DraftOrder

import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductInfoViewModel(private val repository: Repository):ViewModel() {
    private val mutableProductDetailsStateFlow = MutableStateFlow<ApiState<List<Products>>>(ApiState.Loading())
    val productDetailsStateFlow = mutableProductDetailsStateFlow.asStateFlow()
    // StateFlow to hold the API state
    private val _draftOrdersState = MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val draftOrdersState: StateFlow<ApiState<List<ReceivedDraftOrder>>> get() = _draftOrdersState
    private val mutableDraftOrderStateFlow = MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()
    fun getProductDetails(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductDetails (id)
                .catch {
                    mutableProductDetailsStateFlow.value = ApiState.Failure(it.toString())
                    Log.e("TAG", "getProductDetailsViewModel: ${it.message}", )
                }
                .collectLatest{
                    mutableProductDetailsStateFlow.value = ApiState.Success(it.products)
                }
            }
        }



    // Function to fetch all draft orders
    fun fetchAllDraftOrders() {
        viewModelScope.launch {
            _draftOrdersState.value = ApiState.Loading() // Set loading state
            try {
                val draftOrders = repository.getAllDraftOrders()
                _draftOrdersState.value = ApiState.Success(draftOrders) // Set success state
            } catch (e: Exception) {
                _draftOrdersState.value = ApiState.Failure(e.message ?: "An error occurred") // Set failure state
            }
        }
    }

    // Function to create a new draft order
    fun createDraftOrder(draftOrderRequest: DraftOrderRequest) {
        viewModelScope.launch {
            _draftOrdersState.value = ApiState.Loading() // Set loading state
            try {
                val draftOrder = repository.getOrCreateDraftOrder(draftOrderRequest)
                _draftOrdersState.value = ApiState.Success(listOf(draftOrder)) // Return new draft order in success state
            } catch (e: Exception) {
                _draftOrdersState.value = ApiState.Failure(e.message ?: "An error occurred") // Set failure state
            }
        }
    }

    // Function to insert an item into a draft order
    fun insertItemToDraftOrder(draftOrderId: Long, lineItem: DraftOrderRequest) {
        viewModelScope.launch {
            _draftOrdersState.value = ApiState.Loading() // Set loading state
            try {
                val updatedOrder = repository.insertItemToDraftOrder(draftOrderId, lineItem)
                _draftOrdersState.value = ApiState.Success(listOf(updatedOrder)) // Return updated draft order in success state
            } catch (e: Exception) {
                _draftOrdersState.value = ApiState.Failure(e.message ?: "An error occurred") // Set failure state
            }
        }
    }

    // Function to delete an item from a draft order
    fun deleteItemFromDraftOrder(draftOrderId: Long, lineItemId: Long) {
        viewModelScope.launch {
            _draftOrdersState.value = ApiState.Loading() // Set loading state
            try {
                val updatedOrder = repository.deleteItemFromDraftOrder(draftOrderId, lineItemId)
                _draftOrdersState.value = ApiState.Success(listOf(updatedOrder)) // Return updated draft order in success state
            } catch (e: Exception) {
                _draftOrdersState.value = ApiState.Failure(e.message ?: "An error occurred") // Set failure state
            }
        }
    }
    fun getDraftOrder(id:Long){
        viewModelScope.launch(Dispatchers.IO){
            repository.getFavoriteDraftOrder(id).catch {
                mutableDraftOrderStateFlow.value = ApiState.Failure(it.message.toString())

            }.collect{
                mutableDraftOrderStateFlow.value = ApiState.Success(it)

            }
        }
}



    fun getFavoriteDraftOrder(draftOrderId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getFavoriteDraftOrder(draftOrderId).catch {
                mutableDraftOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e("TAG", "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableDraftOrderStateFlow.value = ApiState.Success(it)
                Log.i("TAG", "success getAllDraftOrders")
            }
        }
    }
    private val mutableUpdatedOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    fun updateFavoriteDraftOrder(
        customerId: Long,
        updateDraftOrderRequest: UpdateDraftOrderRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest).catch {
                mutableUpdatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e("TAG", "error updateDraftOrder: ${it.message}")
            }.collect {
                mutableUpdatedOrderStateFlow.value = ApiState.Success(it.draft_order)
                Log.i("TAG", "updateDraftOrder: $it")
            }
        }
    }
}


