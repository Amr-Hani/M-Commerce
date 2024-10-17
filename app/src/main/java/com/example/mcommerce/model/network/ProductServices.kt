package com.example.mcommerce.model.network

import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse
import com.example.mcommerce.model.responses.ProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ProductServices {
    @GET("admin/api/2024-10/products.json")
    suspend fun getProductDetails(@Query("ids") id: Long): ProductResponse

    //@Headers("Content-Type:application/json", "X-Shopify-Access-Token:"+ "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")
    //@Headers("Content-Type:application/json","d095d71e590639b80e989a06e2660a1f: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")
    //@POST("customers.json")

    @POST("admin/api/2024-10/customers.json")
    suspend fun createCustomer(
        @Body customerRequest: CustomerRequest
    ): CustomerResponse
}