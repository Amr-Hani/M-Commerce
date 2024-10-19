package com.example.mcommerceapp.model.network

import com.example.mcommerce.model.network.currency.ExchangeRateApiService
import kotlinx.coroutines.flow.flow

class RemoteDataSourceForCurrency(private val apiService: ExchangeRateApiService):IRemoteDataSource {
    companion object {
        private var instance: RemoteDataSourceForCurrency? = null
        fun getInstance(apiService: ExchangeRateApiService): RemoteDataSourceForCurrency {
            return instance ?: synchronized(this) {
                val result = RemoteDataSourceForCurrency(apiService)
                instance = result
                return result
            }
        }
    }

    override fun getExchangeRates()= flow{
            emit(apiService.getLatestRates())
    }

}