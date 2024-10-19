package com.example.mcommerce.ui.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: Repository):ViewModel() {
    private val TAG = "FavoriteViewModel"

    private val mutableCreatedOrderStateFlow = MutableStateFlow<ApiState<ReceivedDraftOrder>>(ApiState.Loading())
    val createdOrderStateFlow = mutableCreatedOrderStateFlow.asStateFlow()

    private val mutableUpdatedOrderStateFlow = MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    private val mutableAllDraftOrdersStateFlow = MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val allDraftOrdersStateFlow = mutableAllDraftOrdersStateFlow.asStateFlow()


    private val mutableDraftOrderStateFlow = MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()

    private val mutableProductDetailsStateFlow = MutableStateFlow<ApiState<List<Products>>>(ApiState.Loading())
    val productDetailsStateFlow = mutableProductDetailsStateFlow.asStateFlow()

    fun createFavoriteDraftOrder(draftOrderRequest: DraftOrderRequest){
        viewModelScope.launch(Dispatchers.IO){
            repository.createFavoriteDraftOrder(draftOrderRequest).catch {
                mutableCreatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "there was error while creating error: ${it.message}")
            }.collect{
                mutableCreatedOrderStateFlow.value = ApiState.Success(it)
                Log.d(TAG, "createOrder: ${it.id}")
                Log.i(TAG, "created the Order successfully")
            }
        }
    }
    fun updateFavoriteDraftOrder(customerId:Long, updateDraftOrderRequest: UpdateDraftOrderRequest){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest).catch {
                mutableUpdatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error updateDraftOrder: ${it.message}")
            }.collect{
                mutableUpdatedOrderStateFlow.value = ApiState.Success(it.draft_order)
                Log.i(TAG, "updateDraftOrder: $it")
            }
        }
    }

    fun getAllFavoriteDraftOrders(){
        viewModelScope.launch(Dispatchers.IO){
            repository.getAllFavoriteDraftOrders().catch {
                mutableAllDraftOrdersStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect{
                mutableAllDraftOrdersStateFlow.value = ApiState.Success(it.draft_orders)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }

    fun getFavoriteDraftOrder(draftOrderId:Long){
        viewModelScope.launch(Dispatchers.IO){
            repository.getFavoriteDraftOrder(draftOrderId).catch {
                mutableDraftOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e(TAG, "error getAllDraftOrders: ${it.message}")
            }.collect{
                mutableDraftOrderStateFlow.value = ApiState.Success(it)
                Log.i(TAG, "success getAllDraftOrders")
            }
        }
    }



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


}