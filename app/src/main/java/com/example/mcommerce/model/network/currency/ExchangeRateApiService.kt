package com.example.mcommerce.model.network.currency


import com.example.mcommerce.model.responses.currency.ExchangeRateResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface ExchangeRateApiService {
    @GET("latest/{base}")
    suspend fun getLatestRates(@Path("base") baseCurrency: String = "USD"): Response<ExchangeRateResponse>
}

object RetrofitInstance {
    private const val BASE_URL = "https://v6.exchangerate-api.com/v6/24c6dbe125f39a66f8a3a2f7/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ExchangeRateApiService::class.java)

    val exchangeRateApi = retrofit
}