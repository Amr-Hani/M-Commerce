package com.example.mcommerce.ui.order.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.responses.orders.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: Repository) : ViewModel() {
    private val _order = MutableStateFlow<ApiState<List<Order>>>(ApiState.Loading())
    val order = _order.asStateFlow()

    fun getCustomerOrders(id:String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getOrderById(id).collect { draftOrders ->
                    _order.value = ApiState.Success(listOf(draftOrders)  )
                }
            } catch (e: Exception) {
                _order.value = ApiState.Failure(e.message.toString())
            }
        }
    }
}
