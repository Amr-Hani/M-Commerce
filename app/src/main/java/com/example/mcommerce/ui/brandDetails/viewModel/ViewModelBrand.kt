package com.example.mcommerce.ui.brandDetails.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ViewModelBrand (private val repository: Repository):ViewModel(){
    private val _brandDetails =
        MutableStateFlow<ApiState<List<ProductResponse>>>(ApiState.Loading())
    val brandDetails = _brandDetails.asStateFlow()

    fun getproductBrandById(brandId:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProductsByBrandId(brandId)
                .catch {
                    _brandDetails.value = ApiState.Failure(it.message.toString())
                    Log.e("TAG", "getProductDetailsViewModelbyid: ${it.message}",)
                }
                .collect {
                    _brandDetails.value = ApiState.Success(it)
                }
        }
    }}