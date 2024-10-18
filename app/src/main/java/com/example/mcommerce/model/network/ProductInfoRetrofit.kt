package com.example.mcommerce.model.network

import android.util.Base64
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductInfoRetrofit {
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Basic " + Base64.encodeToString("11e78826cf84e3b78e84a8a635e8c91e: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".toByteArray(), Base64.NO_WRAP))
            .build()
        chain.proceed(request)
        }
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
    .build()
    private val gson = GsonBuilder()
        .setLenient() // Allow lenient JSON parsing
        .create()
    private const val BASE_URL = "https://itp-ism-and2.myshopify.com/"
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    val productService = retrofit.create(ShopifyApi::class.java)
}