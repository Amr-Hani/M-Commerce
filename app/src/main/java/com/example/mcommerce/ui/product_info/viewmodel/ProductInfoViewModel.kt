
package com.example.mcommerce.ui.product_info.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Products
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
}

//package com.example.mcommerce.ui.product_info.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.mcommerce.model.network.ApiState
//import com.example.mcommerce.model.network.Repository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.catch
//import kotlinx.coroutines.flow.collectLatest
//import kotlinx.coroutines.launch
//
//class ProductInfoViewModel(private val repository: Repository):ViewModel() {
//    private val mutableProductDetailsStateFlow = MutableStateFlow<ApiState>(ApiState.Loading)
//    val productDetailsStateFlow = mutableProductDetailsStateFlow.asStateFlow()
//    fun getProductDetails(id: Long) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.getProductDetails (id)
//                .catch {
//                    mutableProductDetailsStateFlow.value = ApiState.OnFailed(it)
//                    Log.e("TAG", "getProductDetailsViewModel: ${it.message}", )
//                }
//                .collectLatest{
//                    mutableProductDetailsStateFlow.value = ApiState.OnSuccess(it)
//                }
//            }
//        }
//}

