package com.example.mcommerceapp.model.network


import com.example.mcommerce.model.responses.currency.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRemoteDataSource {
    fun getExchangeRates(): Flow<Response<ExchangeRateResponse>>
}