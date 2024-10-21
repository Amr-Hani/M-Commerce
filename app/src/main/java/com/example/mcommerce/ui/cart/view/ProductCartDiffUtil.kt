package com.example.mcommerce.ui.cart.view

import androidx.recyclerview.widget.DiffUtil
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.prouduct.Product

class ProductCartDiffUtil : DiffUtil.ItemCallback<LineItem>() {

    override fun areItemsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
        return oldItem == newItem
    }
}
