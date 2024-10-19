package com.example.mcommerce.ui.search.viewmodel

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

class SearchFragmentViewModel(private val repository: Repository) : ViewModel() {
    private val mutableSearchProductStateFlow =
        MutableStateFlow<ApiState<List<Products>>>(ApiState.Loading())
    val SearchProductStateFlow = mutableSearchProductStateFlow.asStateFlow()

    fun getProduct() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getProducts().catch {
                mutableSearchProductStateFlow.value = ApiState.Failure(it.message.toString())
            }.collectLatest {
                mutableSearchProductStateFlow.value = ApiState.Success(it.products)
            }
        }
    }

}