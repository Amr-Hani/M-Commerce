package com.example.mcommerce.ui.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: Repository):ViewModel() {
    private val TAG = "FavoriteViewModel"

    private val mutableCreatedOrderStateFlow = MutableStateFlow<ApiState<ReceivedDraftOrder>>(ApiState.Loading())
    val createdOrderStateFlow = mutableCreatedOrderStateFlow.asStateFlow()

    private val mutableUpdatedOrderStateFlow = MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    private val mutableAllDraftOrdersStateFlow = MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val allDraftOrdersStateFlow = mutableAllDraftOrdersStateFlow.asStateFlow()

    fun createOrder(draftOrderRequest: DraftOrderRequest){
        viewModelScope.launch(Dispatchers.IO){
            repository.createDraftOrder(draftOrderRequest).catch {
                mutableCreatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "there was error while creating error: ${it.message}")
            }.collect{
                mutableCreatedOrderStateFlow.value = ApiState.Success(it)
                Log.i(TAG, "created the Order successfully")
            }
        }
    }
    fun updateDraftOrder(customerId:Long, updateDraftOrderRequest: UpdateDraftOrderRequest){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateDraftOrderRequest(customerId, updateDraftOrderRequest).catch {
                mutableUpdatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error updateDraftOrder: ${it.message}")
            }.collect{
                mutableUpdatedOrderStateFlow.value = ApiState.Success(it.draft_order)
                Log.i(TAG, "updateDraftOrder: $it")
            }
        }
    }

    fun getAllDraftOrders(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllDraftOrders().catch {
                mutableAllDraftOrdersStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect{
                mutableAllDraftOrdersStateFlow.value = ApiState.Success(it.draft_orders)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }
}