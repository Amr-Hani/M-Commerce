package com.example.mcommerce.model.network

import com.example.mcommerce.model.responses.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductServices {
    @GET("admin/api/2022-01/products.json")
    suspend fun getProductDetails(@Query("ids") id: Long): ProductResponse
}