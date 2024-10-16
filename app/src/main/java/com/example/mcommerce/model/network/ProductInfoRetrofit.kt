package com.example.mcommerce.model.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductInfoRetrofit {
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Basic " + Base64.encodeToString("d095d71e590639b80e989a06e2660a1f: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".toByteArray(), Base64.NO_WRAP))
            .build()
        chain.proceed(request)
        }
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
    .build()

    private const val BASE_URL = "https://itp-ism-and1.myshopify.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    val productService = retrofit.create(ProductServices::class.java)
}