package com.example.mcommerce.model.network




import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse
import com.example.mcommerce.model.pojos.UpdateCustomerRequest

import SmartCollectionsItem
import com.example.mcommerce.PartialOrder2
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest


import com.example.mcommerce.model.pojos.PriceRule



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


class RemoteDataSource(private val api: ShopifyApi) {

    suspend fun getProductDetails(id: Long): ProductResponse {
        return api.getProductDetails(id)
    }


    suspend fun postCustomer(customer: CustomerRequest): CustomerResponse {
        return api.createCustomer(customer)
    }

    fun getBrands(): Flow<List<SmartCollectionsItem>> = flow {
        val response = api.getvendorBrand().smartCollections
        response?.filterNotNull()?.let { emit(it) }
    }

    fun getProductsByBrandId(id: Long): Flow<List<ProductResponse>> = flow {
        api.getProductsByBrandId(id).let { emit(listOf(it)) }
    }

    fun getProductsBySubCategory(category: String): Flow<List<CustomCollection>> = flow {

        val response = api.getProductsBySubCategory(category).customCollections
        response?.filterNotNull()?.let { emit(it) }
    }

    suspend fun getProducts(): ProductResponse {
        return api.getProducts()
    }

    fun getProductById(id: Long): Flow<ProductResponse> = flow {
        emit(api.getProductById(id))

    }

    suspend fun createFavoriteDraftOrder(draftOrderRequest: DraftOrderRequest): ReceivedDraftOrder {
        return api.createFavoriteDraftOrder(draftOrderRequest)
    }

    suspend fun updateFavoriteDraftOrder(
        customerId: Long,
        updateDraftOrderRequest: UpdateDraftOrderRequest
    ): UpdateDraftOrderRequest {
        return api.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest)
    }

    suspend fun getAllFavoriteDraftOrders(): ReceivedOrdersResponse {
        return api.getAllFavoriteDraftOrders()
    }

    suspend fun getFavoriteDraftOrder(draftOrderId: Long): DraftOrderRequest {
        return api.getFavoriteDraftOrder(draftOrderId)
    }



    suspend fun confirmOrder(ordersItem: Order?): Flow<Order> = flow {
        emit(api.confirmOrder(ordersItem))
    }

    suspend fun getDrafrOrderById(id: String): Flow<ReceivedDraftOrder> = flow {
        emit(api.getDrafrOrderById(id))
    }

    suspend fun getOrderById(id: String): Flow<ReceivedOrdersResponse> = flow {
        emit(api.getOrderById(id))
    }


    //addresses
    suspend fun getAddresses(customerId: Long) = api.getAddresses(customerId)

    suspend fun addAddress(customerId: Long, address: AddAddressResponse) =
        api.addAddress(customerId, address)

    suspend fun deleteAddress(customerId: Long, addressId: Long) =
        api.deleteAddress(customerId, addressId)

    // Fetch coupons from API
    fun getCoupons(): Flow<Map<String, PriceRule>> = flow {
        val response = api.getCoupons()
        // Convert the response to a Map for easier usage
        val couponMap = response.price_rules.associate { it.title to it as PriceRule }
        emit(couponMap)
    }

    suspend fun delCartItem(id: String) {
        api.delCartItem(id)
    }


    // Create a new draft order
    suspend fun createDraftOrder(request: DraftOrderRequest): ReceivedDraftOrder {
        return api.createFavoriteDraftOrder(request)
    }

    // Update an existing draft order
    suspend fun updateDraftOrder(
        draftOrderId: Long,
        request: UpdateDraftOrderRequest
    ): ReceivedDraftOrder {
        return api.updateDraftOrder(draftOrderId, request)
    }

    // Get all favorite draft orders
    suspend fun getAllDraftOrders(): List<ReceivedDraftOrder> {
        return api.getAllFavoriteDraftOrders().draft_orders
    }

    // Get a specific draft order
    suspend fun getDraftOrder(draftOrderId: Long): DraftOrderRequest {
        return api.getFavoriteDraftOrder(draftOrderId)
    }

    // Insert item into a draft order
    suspend fun insertItemToDraftOrder(
        draftOrderId: Long,
        lineItem: DraftOrderRequest
    ): ReceivedDraftOrder {
        val existingOrder = getDraftOrder(draftOrderId)
        // Ensure line_items is the correct type from the existing order
        val updatedLineItems = existingOrder.draft_order.line_items.toMutableList().apply {
            add(lineItem.draft_order.line_items.get(0))
        }
        val updatedRequest = UpdateDraftOrderRequest(
            DraftOrder(
                updatedLineItems,
                existingOrder.draft_order.applied_discount,
                existingOrder.draft_order.customer,
                existingOrder.draft_order.use_customer_default_address
            )
        )
        return updateDraftOrder(draftOrderId, updatedRequest)
    }

    // Delete an item from a draft order
    suspend fun deleteItemFromDraftOrder(draftOrderId: Long, lineItemId: Long): ReceivedDraftOrder {
        val existingOrder = getDraftOrder(draftOrderId)
        val updatedLineItems =
            existingOrder.draft_order.line_items.filterNot { it.id == lineItemId }
        val updatedRequest = UpdateDraftOrderRequest(
            DraftOrder(
                updatedLineItems,
                existingOrder.draft_order.applied_discount,
                existingOrder.draft_order.customer,
                existingOrder.draft_order.use_customer_default_address
            )
        )
        return updateDraftOrder(draftOrderId, updatedRequest)
    }


    suspend fun deleteFavoriteDraftOrder(customerId: Long) {
        api.deleteFavoriteDraftOrder(customerId)
    }

    suspend fun getCustomerByEmail(customerEmail: String): CustomersByEmailResponse {
        return api.getCustomerByEmail(customerEmail)
    }

    suspend fun updateCustomerById(
        updateCustomerById: Long,
        updateCustomerRequest: UpdateCustomerRequest
    ): CustomerResponse {
        return api.updateCustomerById(updateCustomerById, updateCustomerRequest)
    }

    suspend fun getCustomerById(customerId: Long): CustomerResponse {
        return api.getCustomerById(customerId)
    }


    suspend fun createOrder(partialOrder2: PartialOrder2): OrderElement {
        return api.createOrder(partialOrder2)
    }

    suspend fun getOrdersByCustomerId(customerId: String): Order {
        return api.getOrdersByCustomerId("customer_id:${customerId}")
    }


}





