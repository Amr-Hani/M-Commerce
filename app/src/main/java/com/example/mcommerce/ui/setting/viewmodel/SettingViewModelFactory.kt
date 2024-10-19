package com.example.mcommerceapp.ui.setting.veiwmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mcommerce.model.network.Repository
import com.example.mcommerceapp.model.network.IRepo


class SettingViewModelFactory (private val repository: Repository,private val repo: IRepo) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            SettingViewModel(repository,repo) as T
        } else {
            throw IllegalArgumentException(" class view  model not found ")
        }
    }
}