package com.example.mcommerce.model.network


import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse


import SmartCollectionsItem
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest


import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSource(private val productServices: ProductServices) {
    suspend fun getProductDetails(id: Long): ProductResponse {
        return productServices.getProductDetails(id)
    }


    suspend fun postCustomer(customer: CustomerRequest): CustomerResponse {
        return productServices.createCustomer(customer)
    }

    fun getBrands(): Flow<List<SmartCollectionsItem>> = flow {
        val response = productServices.getvendorBrand().smartCollections
        response?.filterNotNull()?.let { emit(it) }
    }
     fun getProductsByBrandId(id: Long): Flow<List<ProductResponse>> = flow {
        productServices.getProductsByBrandId(id).let { emit(listOf(it) ) }
    }
    fun getProductsBySubCategory(category: String): Flow<List<CustomCollection>> = flow {
        val response = productServices.getProductsBySubCategory(category).customCollections
        response?.filterNotNull()?.let { emit(it) }
    }
    suspend fun getProducts(): ProductResponse {
        return productServices.getProducts()
    }
     fun getProductById(id: Long): Flow<ProductResponse> = flow {
        emit(productServices.getProductById(id))
    }

    suspend fun createFavoriteDraftOrder(draftOrderRequest: DraftOrderRequest): ReceivedDraftOrder {
        return productServices.createFavoriteDraftOrder(draftOrderRequest)
    }

    suspend fun updateFavoriteDraftOrder(customerId: Long, updateDraftOrderRequest: UpdateDraftOrderRequest):UpdateDraftOrderRequest{
        return productServices.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest)
    }

    suspend fun getAllFavoriteDraftOrders(): ReceivedOrdersResponse {
        return productServices.getAllFavoriteDraftOrders()
    }

    suspend fun getFavoriteDraftOrder(draftOrderId:Long): DraftOrderRequest {
        return productServices.getFavoriteDraftOrder(draftOrderId)
    }



}