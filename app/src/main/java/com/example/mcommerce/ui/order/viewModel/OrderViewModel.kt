package com.example.mcommerce.ui.order.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.model.responses.orders.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: Repository) : ViewModel() {
    private val _order = MutableStateFlow<ApiState<List<DraftOrderRequest>>>(ApiState.Loading())
    val order = _order.asStateFlow()

    fun getCustomerOrders(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getFavoriteDraftOrder(id).collect { draftOrders ->
                    _order.value = ApiState.Success(listOf(draftOrders)  )
                    Log.d("apiState", "getCustomerOrders:$draftOrders ")
                }
            } catch (e: Exception) {
                _order.value = ApiState.Failure(e.message.toString())
                Log.d("apiState", "getCustomerOrders:$e ")
            }
        }
    }
    private val _getOrdersByCustomerIdStateFlow = MutableStateFlow<ApiState<Order>>(ApiState.Loading())
    var  getOrdersByCustomerIdStateFlow: StateFlow<ApiState<Order>> = _getOrdersByCustomerIdStateFlow

    fun getOrdersByCustomerId(customerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOrdersByCustomerId(customerId)
                .catch {
                    _getOrdersByCustomerIdStateFlow.value = ApiState.Failure(it.message ?: "Error")
                    Log.d("ApiState", "getCartid:$it ")
                }
                .collect {
                    _getOrdersByCustomerIdStateFlow.value = ApiState.Success(it)
                    Log.d("ApiState", "getCartid:$it ")
                }
        }
    }
}
