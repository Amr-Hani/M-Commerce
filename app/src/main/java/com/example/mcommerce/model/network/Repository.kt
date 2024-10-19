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

class Repository private constructor(private val remoteDataSource: RemoteDataSource) {
    companion object {
        private var INSTANCE: Repository? = null
        fun getInstance(
            remoteDataSource: RemoteDataSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(remoteDataSource)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getProductDetails(id: Long): Flow<ProductResponse> {
        return flow {
            val ProductDetails = remoteDataSource.getProductDetails(id)
            emit(ProductDetails)
        }
    }

    fun postCustomer(customer: CustomerRequest): Flow<CustomerResponse> {
        return flow {
            val PostCustomer = remoteDataSource.postCustomer(customer)
            emit(PostCustomer)
        }
    }


    fun getBrands(): Flow<List<SmartCollectionsItem>> {
        return remoteDataSource.getBrands()
    }

    fun getProductsByBrandId(id: Long): Flow<List<ProductResponse>> {
        return remoteDataSource.getProductsByBrandId(id)
    }

    fun getProductsBySubCategory(category: String): Flow<List<CustomCollection>> {
        return remoteDataSource.getProductsBySubCategory(category)
    }

    fun getProductById(id: Long): Flow<List<ProductResponse>> {
        return remoteDataSource.getProductsByBrandId(id)
    }

    fun getProducts(): Flow<ProductResponse> {
        return flow {
            val result = remoteDataSource.getProducts()
            emit(result)
        }
    }

    fun createFavoriteDraftOrder(draftOrderRequest: DraftOrderRequest): Flow<ReceivedDraftOrder> {
        return flow {
            emit(remoteDataSource.createFavoriteDraftOrder(draftOrderRequest))
        }
    }

    fun updateFavoriteDraftOrder(
        customerId: Long,
        updateDraftOrderRequest: UpdateDraftOrderRequest
    ): Flow<UpdateDraftOrderRequest> {
        return flow {
            emit(remoteDataSource.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest))
        }
    }

    fun getAllFavoriteDraftOrders(): Flow<ReceivedOrdersResponse> {
        return flow {
            emit(remoteDataSource.getAllFavoriteDraftOrders())
        }
    }


    fun getFavoriteDraftOrder(draftOrderId: Long): Flow<DraftOrderRequest> {
        return flow {
            emit(
                remoteDataSource.getFavoriteDraftOrder(draftOrderId)
            )
        }
    }

}



