package com.example.mcommerce.ui.order.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: Repository) : ViewModel() {
    private val _order = MutableStateFlow<ApiState<List<ReceivedDraftOrder>>>(ApiState.Loading())
    val order = _order.asStateFlow()

    fun getCustomerOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getAllDraftOrders().collect { draftOrders ->
                    _order.value = ApiState.Success(draftOrders.draft_orders)
                }
            } catch (e: Exception) {
                _order.value = ApiState.Failure(e.message.toString())
            }
        }
    }
}
