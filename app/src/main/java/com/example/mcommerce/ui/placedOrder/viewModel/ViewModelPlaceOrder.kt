package com.example.mcommerce.ui.placedOrder.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.responses.orders.Order
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ViewModelPlaceOrder(private val repo:Repository):ViewModel() {


    var isPayPalChoose=false
    var isPaymentApproved=false
    var orderResponse: Order?=null
    private val _uiState = MutableStateFlow<ApiState<Order>>(ApiState.Loading())
    var uiState: StateFlow<ApiState<Order>> = _uiState
    private val _isLoading= MutableLiveData(false)
    private val _cart : MutableStateFlow<ApiState<ReceivedDraftOrder>> = MutableStateFlow(
        ApiState.Loading())
    val cart : StateFlow<ApiState<ReceivedDraftOrder>> = _cart
    var isLoading: LiveData<Boolean> =_isLoading
//    var lineItemsList:MutableList<ReceivedDraftOrder> = mutableListOf()

    //دي فانكشن انا هستخدمخا لما يجي الكلاينت يدوس ع زرار الدفع سواء كاش او فيزا
    fun confirmOrder() {
        viewModelScope.launch {
            _isLoading.value=true
            _uiState.value = ApiState.Loading()
            if((isPaymentApproved && isPayPalChoose) || !isPayPalChoose) {
                Log.i("TAG", "confirmOrder: ${orderResponse}")
                orderResponse?.let {
                    repo.confirmOrder(it).catch {
                        _isLoading.value = false
                        _uiState.value = it.localizedMessage?.let { it1 -> ApiState.Failure("server down, please try again later: $it1 ") }!!
                    }.collect { orderItems ->
                        _isLoading.value = false
                        _uiState.value = ApiState.Success(orderItems)
                    }
                }
            }
        }
    }
    fun deleteCartItemsById(id:String){
        viewModelScope.launch {

            repo.delCartItem(id)


        }
    }
    fun getCart(id: String)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDraftOrderById(id)
                .catch {
                    _cart.value = ApiState.Failure(it.message ?: "Error")
                }
                .collect{
//                    lineItemsList.clear()
//                    lineItemsList.addAll(listOf(it))
                    _cart.value = ApiState.Success(it)
                }
        }
    }

}