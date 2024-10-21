
package com.example.mcommerce.ui.product_info.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductInfoViewModel(private val repository: Repository):ViewModel() {
    private val mutableProductDetailsStateFlow = MutableStateFlow<ApiState<List<Products>>>(ApiState.Loading())
    val productDetailsStateFlow = mutableProductDetailsStateFlow.asStateFlow()
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

    private val mutableDraftOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()

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

