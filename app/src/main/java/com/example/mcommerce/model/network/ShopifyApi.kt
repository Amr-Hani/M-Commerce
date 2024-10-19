package com.example.mcommerce.model.network


import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse
import com.example.mcommerce.model.responses.ProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

import BrandsPOJO
import com.example.mcommerce.model.pojos.CategoryPOJO

import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.address.AddAddressResponse
import com.example.mcommerce.model.responses.address.AddressResponse
import retrofit2.Response
import retrofit2.http.DELETE


import retrofit2.http.Path

import retrofit2.http.Query

interface ShopifyApi {
    @GET("admin/api/2024-10/products.json")
    suspend fun getProductDetails(@Query("ids") id: Long): ProductResponse


    //@Headers("Content-Type:application/json", "X-Shopify-Access-Token:"+ "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")
    //@Headers("Content-Type:application/json","d095d71e590639b80e989a06e2660a1f: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")
    //@POST("customers.json")

    @POST("admin/api/2024-10/customers.json")
    suspend fun createCustomer(
        @Body customerRequest: CustomerRequest
    ): CustomerResponse

    @GET("admin/api/2022-01/smart_collections.json")
    suspend fun getvendorBrand(): BrandsPOJO

    @GET("admin/api/2022-01/products.json")
    suspend fun getProductsByBrandId(
        @Query("collection_id") brandId:Long): ProductResponse

    @GET(  "admin/api/2024-10/custom_collections.json")
    suspend fun getProductsBySubCategory(@Query("product_type") subCategory: String):CategoryPOJO
    @GET("products.json")
    suspend fun getProducts(): Products

    suspend fun getProductById(@Path("product_id") productId:Long):ProductResponse

    @GET("admin/api/2024-10/customers/{customerId}/addresses.json")
    suspend fun getAddresses(
        @Path("customerId") customerId: Long
    ): AddressResponse

    @POST("admin/api/2024-10/customers/{customerId}/addresses.json")
    suspend fun addAddress(
        @Path("customerId") customerId: Long,
        @Body address: AddAddressResponse
    ): Response<AddAddressResponse>

    @DELETE("admin/api/2024-10/customers/{customerId}/addresses/{addressId}.json")
    suspend fun deleteAddress(
        @Path("customerId") customerId: Long,
        @Path("addressId") addressId: Long
    ): Response<Unit>
}