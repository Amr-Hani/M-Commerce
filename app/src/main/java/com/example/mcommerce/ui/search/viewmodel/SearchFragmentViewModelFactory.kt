package com.example.mcommerce.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModel

class SearchFragmentViewModelFactory(private val repository: Repository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SearchFragmentViewModel::class.java)) {
            SearchFragmentViewModel(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}