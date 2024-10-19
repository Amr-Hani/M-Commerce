package com.example.mcommerce.model.pojos.prouduct

data class Product(
    val image: String,
    val name: String,
    val price: Double,
    var quantity: Int,
    val stock: Int
)