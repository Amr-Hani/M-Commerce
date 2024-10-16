package com.example.mcommerce.model.network

import com.example.mcommerce.model.responses.ProductResponse

class RemoteDataSource(private val productServices: ProductServices) {
    suspend fun getProductDetails(id: Long): ProductResponse {
        return productServices.getProductDetails(id)
    }
}