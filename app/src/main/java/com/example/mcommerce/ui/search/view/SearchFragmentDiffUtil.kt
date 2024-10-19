package com.example.mcommerce.ui.search.view

import androidx.recyclerview.widget.DiffUtil
import com.example.mcommerce.model.pojos.Products

class SearchFragmentDiffUtil: DiffUtil.ItemCallback<Products>() {
    override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
        return oldItem== newItem
    }
}