package com.example.mcommerceapp.model.network

import com.example.mcommerce.model.responses.currency.ExchangeRateResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRepo {
    fun getLatestExchangeRates(): Flow<Response<ExchangeRateResponse>>
}

class Repo(val remoteDataSource: RemoteDataSourceForCurrency) : IRepo {
    companion object {

        private var instance: Repo? = null

        fun getInstance(
            remoteDataSource: RemoteDataSourceForCurrency
        ): Repo {
            return instance ?: synchronized(this) {
                val repo = Repo(remoteDataSource)
                instance = repo
                repo
            }
        }
    }

    override fun getLatestExchangeRates()= remoteDataSource.getExchangeRates()
}