package com.example.mcommerce.ui.cart.view

import androidx.recyclerview.widget.DiffUtil
import com.example.mcommerce.model.pojos.prouduct.Product

class ProductCartDiffUtil : DiffUtil.ItemCallback<Product>() {

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
