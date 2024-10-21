package com.example.mcommerce.ui.dummy.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerce.model.network.Repository


class DummyViewModelFactory (private val repository: Repository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ViewModelDummy::class.java)) {
            ViewModelDummy(repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}