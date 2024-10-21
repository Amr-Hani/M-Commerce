package com.example.mcommerce.ui.search.view

interface OnFavoriteClick<T> {
    fun onFavoriteClick(favorite: T)
}
interface OnDetailsClick<T>{
    fun onDetailsClick(details:T)
}