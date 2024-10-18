package com.example.mcommerceapp.ui.setting.veiwmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.responses.AddAddressResponse
import com.example.mcommerce.model.responses.Address
import com.example.mcommerce.model.responses.CustomerAddress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SettingViewModel(private val repository: Repository) : ViewModel() {
    private val _addressState = MutableStateFlow<ApiState<List<Address>>>(ApiState.Loading())
    val addressState: StateFlow<ApiState<List<Address>>> = _addressState
//init {
//    loadAddresses(8229253710128)
//}

//    private val _exchangeRatesState = MutableStateFlow<UIState>(UIState.Loading)
//    val exchangeRatesState: StateFlow<UIState> get() = _exchangeRatesState
//
//    private val _addressState = MutableStateFlow<UIState>(UIState.Loading)
//    val addressState: StateFlow<UIState> get() = _addressState
//    init {
//        loadAddresses()
//        //fetchLatestRates()
//    }
//    fun getAddressListSize(): Int {
//        val gson = Gson()
//        val addressesJson = sharedPreferences.getString("addresses", null)
//        return if (addressesJson != null) {
//            val addressType = object : TypeToken<List<Address>>() {}.type
//            val addresses = gson.fromJson<List<Address>>(addressesJson, addressType)
//            addresses.size
//        } else {
//            0
//        }
//    }
//    fun loadAddresses() {
//
//        viewModelScope.launch {
//            _addressState.value = UIState.Loading
//            val gson = Gson()
//            val addressesJson = sharedPreferences.getString("addresses", null)
//            if (addressesJson != null) {
//                val addressType = object : TypeToken<List<Address>>() {}.type
//                val addresses = gson.fromJson<List<Address>>(addressesJson, addressType)
//                _addressState.value = UIState.Success(addresses)
//            } else {
//                _addressState.value = UIState.Failure(Throwable("No addresses found"))
//            }
//        }
//
//    }
//
//    fun deleteAddress(position: Int) {
//
//        viewModelScope.launch {
//            val gson = Gson()
//            val addressesJson = sharedPreferences.getString("addresses", null)
//            val addressType = object : TypeToken<MutableList<Address>>() {}.type
//            val addresses = if (addressesJson != null) {
//                gson.fromJson<MutableList<Address>>(addressesJson, addressType)
//            } else {
//                mutableListOf<Address>()
//            }
//
//            if (addresses.isNotEmpty() && position in addresses.indices) {
//                addresses.removeAt(position)
//                val updatedAddressesJson = gson.toJson(addresses)
//                sharedPreferences.edit().putString("addresses", updatedAddressesJson).apply()
//
//                _addressState.value = UIState.Success(addresses)
//            } else {
//                _addressState.value = UIState.Failure(Throwable("Invalid position: $position"))
//            }
//        }
//    }
//
//
//    fun addAddress(newAddress: Address) {
//        viewModelScope.launch {
//            val gson = Gson()
//            val addressesJson = sharedPreferences.getString("addresses", null)
//            val addressType = object : TypeToken<MutableList<Address>>() {}.type
//            val addresses = if (addressesJson != null) {
//                gson.fromJson<MutableList<Address>>(addressesJson, addressType)
//            } else {
//                mutableListOf<Address>()
//            }
//            addresses.add(newAddress)
//
//            val updatedAddressesJson = gson.toJson(addresses)
//            sharedPreferences.edit().putString("addresses", updatedAddressesJson).apply()
//
//            _addressState.value = UIState.Success(addresses)
//        }
//    }





    // Fetch addresses and emit appropriate states
    fun loadAddresses(customerId: Long) {
        viewModelScope.launch {
            _addressState.value = ApiState.Loading()  // Emit loading state
            try {
                Log.d("API Request", "Loading addresses for customerId: $customerId")
                val response = repository.getAddresses(customerId)
                Log.d("API Response", "Addresses received: ${response.addresses?.size}")

                // Check for nullability and assign the state
                _addressState.value = ApiState.Success(response.addresses ?: emptyList())
            } catch (e: HttpException) {
                // Log specific error response from API
                Log.d("API Error", "HttpException: ${e.code()} - ${e.message()}")
                _addressState.value = ApiState.Failure("Error: ${e.message()}")
            } catch (e: IOException) {
                Log.d("API Error", "IOException: ${e.message}")
                _addressState.value = ApiState.Failure("Network error: ${e.message}")
            } catch (e: Exception) {
                Log.d("API Error", "Unexpected error: ${e.localizedMessage}")
                _addressState.value = ApiState.Failure("Error: ${e.localizedMessage}")
            }
        }
    }

    // Add a new address and refresh the list
    fun addAddress(customerId: Long, address: CustomerAddress) {
        Log.d("response", "Attempting to add address for customer: $customerId")
        viewModelScope.launch {
            _addressState.value = ApiState.Loading() // Set loading state before making the API call
            try {
                // Call repository to add address, expecting an AddAddressResponse
                val addAddressResponse = AddAddressResponse(address)
                val response = repository.addAddress(customerId, addAddressResponse)
                Log.d("response", "API response at add: ${response}")

                if (response.isSuccessful) {
                    // Log success and process the response to get the added address
                    val addedAddress = response.body()?.customer_address
                    Log.d("response", "Address added successfully: $addedAddress")

                    loadAddresses(customerId)  // Refresh the list after successful addition
                } else {
                    // Log failure case with detailed error message
                    Log.d("response", "Failed to add address: ${response.message()}")
                    _addressState.value = ApiState.Failure("Failed to add address: ${response.message()}")
                }
            } catch (e: Exception) {
                // Log exception message and update state with error
                Log.e("response", "Exception during address addition: ${e.message}")
                _addressState.value = ApiState.Failure("Error: ${e.localizedMessage}")
            }
        }
    }


    // Delete an address and refresh the list
    fun deleteAddress(customerId: Long, addressId: Long) {
        viewModelScope.launch {
            _addressState.value = ApiState.Loading()
            try {
                val response = repository.deleteAddress(customerId, addressId)
                if (response.isSuccessful) {
                    loadAddresses(customerId)  // Refresh after successful deletion
                } else {
                    _addressState.value = ApiState.Failure("Failed to delete address: ${response.message()}")
                }
            } catch (e: Exception) {
                _addressState.value = ApiState.Failure("Error: ${e.localizedMessage}")
            }
        }
    }

//    private fun fetchLatestRates() {
//        viewModelScope.launch {
//            repository.getLatestExchangeRates().collect { response ->
//                try {
//                    if (response.isSuccessful) {
//                        response.body()?.let { data ->
//                            _exchangeRatesState.value = UIState.Success(data.conversion_rates["EGP"])
//                            val x= data.conversion_rates["EGP"]
//                            Log.d("Transaction", "fetchLatestRates: ${x?.times(50)}")
//                        } ?: run {
//                            _exchangeRatesState.value = UIState.Failure(NullPointerException("No data available"))
//                        }
//                    } else {
//                        _exchangeRatesState.value = UIState.Failure(HttpException(response))
//                    }
//                } catch (e: IOException) {
//                    _exchangeRatesState.value = UIState.Failure(e)
//                } catch (e: Exception) {
//                    _exchangeRatesState.value = UIState.Failure(e)
//                }
//            }
//        }
//    }
}
