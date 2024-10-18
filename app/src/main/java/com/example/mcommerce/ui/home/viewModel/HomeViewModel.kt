package com.example.mcommerce.ui.home.viewModel

import SmartCollectionsItem
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

class HomeViewModel(private val repository: Repository) : ViewModel() {
    private val _productsbrandId =
        MutableStateFlow<ApiState<List<SmartCollectionsItem>>>(ApiState.Loading())
    val productsbrandId = _productsbrandId.asStateFlow()

    fun getbrands() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getBrands ()
                .catch {
                    _productsbrandId.value = ApiState.Failure(it.message.toString())

                }
                .collectLatest{
                    _productsbrandId.value = ApiState.Success(it)
                    Log.d("it", "getbrands:$it ")
                }
        }
    }


}