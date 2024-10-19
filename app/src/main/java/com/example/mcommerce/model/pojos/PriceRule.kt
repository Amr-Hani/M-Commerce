package com.example.mcommerce.model.pojos

data class PriceRule(
    val id: Long,
    val value_type: String,
    val value: String,
    val title: String,
    val starts_at: String,
    val ends_at: String
)