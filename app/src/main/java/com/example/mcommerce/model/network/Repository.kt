package com.example.mcommerce.model.network


import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse


import SmartCollectionsItem
import com.example.mcommerce.PartialOrder2
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.DraftOrderRequest

import com.example.mcommerce.model.pojos.PriceRule

import com.example.mcommerce.model.pojos.UpdateCustomerRequest


import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.CustomersByEmailResponse
import com.example.mcommerce.model.responses.address.AddAddressResponse
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.model.responses.orders.Order
import com.example.mcommerce.model.responses.orders.OrderElement
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

    suspend fun getAddresses(customerId: Long) = remoteDataSource.getAddresses(customerId)

    suspend fun addAddress(customerId: Long, address: AddAddressResponse) =
        remoteDataSource.addAddress(customerId, address)

    suspend fun deleteAddress(customerId: Long, addressId: Long) =
        remoteDataSource.deleteAddress(customerId, addressId)

    // Fetch coupons from RemoteDataSource
    fun getCoupons(): Flow<Map<String, PriceRule>> {
        return remoteDataSource.getCoupons()
    }

    // Create or get a draft order
    suspend fun getOrCreateDraftOrder(draftOrderRequest: DraftOrderRequest): ReceivedDraftOrder {
        // Check if the draft order already exists
        val existingOrders = remoteDataSource.getAllDraftOrders()
        val existingOrder = existingOrders.find { it.equals(draftOrderRequest.draft_order) }

        return if (existingOrder != null) {
            existingOrder // Return existing order
        } else {
            remoteDataSource.createDraftOrder(draftOrderRequest) // Create new order
        }
    }

    // Insert item into a draft order
    suspend fun insertItemToDraftOrder(
        draftOrderId: Long,
        lineItem: DraftOrderRequest
    ): ReceivedDraftOrder {
        return remoteDataSource.insertItemToDraftOrder(draftOrderId, lineItem)
    }

    // Delete item from a draft order
    suspend fun deleteItemFromDraftOrder(draftOrderId: Long, lineItemId: Long): ReceivedDraftOrder {
        return remoteDataSource.deleteItemFromDraftOrder(draftOrderId, lineItemId)
    }

    // Get draft order by ID
    suspend fun getDraftOrder(draftOrderId: Long): DraftOrderRequest {
        return remoteDataSource.getDraftOrder(draftOrderId)
    }

    // Get all draft orders
    suspend fun getAllDraftOrders(): List<ReceivedDraftOrder> {
        return remoteDataSource.getAllDraftOrders()
    }

    suspend fun deleteFavoriteDraftOrder(customerId: Long) {
        remoteDataSource.deleteFavoriteDraftOrder(customerId)
    }

    fun getCustomerByEmail(customerEmail: String): Flow<CustomersByEmailResponse> {
        return flow {
            emit(
                remoteDataSource.getCustomerByEmail(customerEmail)
            )
        }
    }

    fun updateCustomerById(
        updateCustomerById: Long,
        updateCustomerRequest: UpdateCustomerRequest
    ): Flow<CustomerResponse> {
        return flow {
            emit(
                remoteDataSource.updateCustomerById(updateCustomerById, updateCustomerRequest)
            )
        }
    }

    fun getCustomerById(customerId: Long): Flow<CustomerResponse> {
        return flow {
            emit(
                remoteDataSource.getCustomerById(customerId)
            )
        }
    }


    suspend fun delCartItem(id: String) {
        return remoteDataSource.delCartItem(id)
    }

    suspend fun confirmOrder(ordersItem: Order?): Flow<Order> {
        return remoteDataSource.confirmOrder(ordersItem)
    }

    suspend fun getDraftOrderById(id: String): Flow<ReceivedDraftOrder> {
        return remoteDataSource.getDrafrOrderById(id)
    }

    suspend fun getOrderById(id: String): Flow<ReceivedOrdersResponse> {
        return remoteDataSource.getOrderById(id)
    }

    suspend fun createOrder(partialOrder2: PartialOrder2): Flow<OrderElement> {
        return flow { emit(remoteDataSource.createOrder(partialOrder2)) }
    }

    suspend fun getOrdersByCustomerId(customerId: String): Flow<Order> {
        return flow { emit(remoteDataSource.getOrdersByCustomerId(customerId)) }
    }


}



