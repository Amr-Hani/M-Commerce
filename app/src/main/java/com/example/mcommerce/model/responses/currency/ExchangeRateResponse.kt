package com.example.mcommerce.model.responses.currency

data class ExchangeRateResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
