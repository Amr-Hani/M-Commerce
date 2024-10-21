package com.example.mcommerce.ui.favorite.view

import com.example.mcommerce.model.pojos.LineItem

interface OnFavoriteClick {
    fun onFavoriteClick(lineItem: LineItem)
}
interface OnProductDetails{
    fun onProductDetails(productId:Long)
}