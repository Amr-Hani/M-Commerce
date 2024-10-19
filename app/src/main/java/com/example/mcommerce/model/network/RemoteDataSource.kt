package com.example.mcommerce.model.network




import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.responses.CustomerResponse


import SmartCollectionsItem
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.address.AddAddressResponse


import com.example.mcommerce.model.responses.ProductResponse
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
        api.getProductsByBrandId(id).let { emit(listOf(it) ) }
    }
    fun getProductsBySubCategory(category: String): Flow<List<CustomCollection>> = flow {
        val response = api.getProductsBySubCategory(category).customCollections
        response.let { emit(it) }
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
}