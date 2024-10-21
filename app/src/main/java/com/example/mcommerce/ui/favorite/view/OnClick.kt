package com.example.mcommerce.ui.favorite.view

import com.example.mcommerce.model.pojos.LineItem

interface OnClick {
    fun onClick(lineItem: LineItem)
}