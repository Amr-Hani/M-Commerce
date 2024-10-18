package com.example.mcommerce.model.network


import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse



import SmartCollectionsItem
import com.example.mcommerce.model.pojos.CustomCollection

import com.example.mcommerce.model.responses.AddAddressResponse

import com.example.mcommerce.model.responses.ProductResponse
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
   fun getProductById(id: Long):Flow<List<ProductResponse>> {
        return remoteDataSource.getProductsByBrandId(id)
    }
    suspend fun getAddresses(customerId: Long) = remoteDataSource.getAddresses(customerId)

    suspend fun addAddress(customerId: Long, address: AddAddressResponse) = remoteDataSource.addAddress(customerId, address)

    suspend fun deleteAddress(customerId: Long, addressId: Long) = remoteDataSource.deleteAddress(customerId, addressId)
}



