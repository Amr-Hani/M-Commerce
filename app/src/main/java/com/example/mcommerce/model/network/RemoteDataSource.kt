package com.example.mcommerce.model.network




import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse


import SmartCollectionsItem
import android.util.Log
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.Order
import com.example.mcommerce.model.pojos.PostDraftOrderItemModel
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.address.AddAddressResponse


import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.GET


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
        api.getProductsByBrandId(id).let { emit(listOf(it) ) }
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

    suspend fun updateFavoriteDraftOrder(customerId: Long, updateDraftOrderRequest: UpdateDraftOrderRequest):UpdateDraftOrderRequest{
        return api.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest)
    }

    suspend fun getAllFavoriteDraftOrders(): ReceivedOrdersResponse {
        return api.getAllFavoriteDraftOrders()
    }

    suspend fun getFavoriteDraftOrder(draftOrderId:Long): DraftOrderRequest {
        return api.getFavoriteDraftOrder(draftOrderId)
    }


    suspend fun getAllDraftOrders(): ReceivedOrdersResponse{
        return api.getAllDraftOrders()
    }
    suspend fun confirmOrder(ordersItem: Order):Flow<Order> = flow {
        emit(api.confirmOrder(ordersItem))
    }








    //addresses
    suspend fun getAddresses(customerId: Long) = api.getAddresses(customerId)


    suspend fun addAddress(customerId: Long, address: AddAddressResponse) = api.addAddress(customerId, address)

    suspend fun deleteAddress(customerId: Long, addressId: Long) = api.deleteAddress(customerId, addressId)
    // Fetch coupons from API
    fun getCoupons() = flow {
        val response = api.getCoupons()
        // Convert the response to a Map for easier usage
        val couponMap = response.price_rules.associate { it.title to it.value }
        emit(couponMap)
    }
   suspend fun delCartItem(id: String) {
        api.delCartItem(id)
    }

        }
