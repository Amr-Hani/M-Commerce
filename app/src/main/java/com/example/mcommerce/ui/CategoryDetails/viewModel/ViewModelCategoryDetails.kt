package com.example.mcommerce.ui.CategoryDetails.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.responses.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class viewModelCategoryDetails(private val repo: Repository) : ViewModel() {
   private val _categoryDetails = MutableStateFlow<ApiState<List<ProductResponse>>>(ApiState.Loading())
    val categoryDetails = _categoryDetails.asStateFlow()

    fun getProductCategoryById(id:Long) {
        viewModelScope.launch(Dispatchers.IO) {
            // استدعاء الدالة الخاصة بك لجلب البيانات بناءً على النوع
            repo.getProductById(id)
                .catch { exception ->
                    _categoryDetails.value = ApiState.Failure(exception.message.toString())
                    Log.e("TAG", "Error fetching category: ${exception.message}")
                }
                .collect { products ->
                    _categoryDetails.value = ApiState.Success(products)
                }
        }
    }}