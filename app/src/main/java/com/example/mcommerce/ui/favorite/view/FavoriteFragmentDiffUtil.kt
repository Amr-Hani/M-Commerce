package com.example.mcommerce.ui.favorite.view

import androidx.recyclerview.widget.DiffUtil
import com.example.mcommerce.model.pojos.LineItem

class FavoriteFragmentDiffUtil : DiffUtil.ItemCallback<LineItem>() {
    override fun areItemsTheSame(oldItem: LineItem, newItem: LineItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: LineItem,
        newItem: LineItem
    ): Boolean {
        return oldItem == newItem
    }
}