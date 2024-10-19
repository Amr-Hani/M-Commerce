//package com.example.mcommerce.ui.order.viewModel
//
//import DraftOrder
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.mcommerce.model.network.ApiState
//import com.example.mcommerce.model.network.Repository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.launch
//
//class OrderViewModel(private val repository: Repository) : ViewModel() {
//    private val _order = MutableStateFlow<ApiState<List<DraftOrder>>>(ApiState.Loading())
//    val order = _order.asStateFlow()
//
//    fun getCustomerOrders(customerId: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                repository.getDraftOrder(customerId).collect { draftOrders ->
//                    _order.value = ApiState.Success(draftOrders) // تأكد من تمرير بيانات صحيحة
//                }
//            } catch (e: Exception) {
//                _order.value = ApiState.Failure(e.message.toString())
//            }
//        }
//    }
//}
