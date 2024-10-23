package com.example.mcommerce.ui.dummy.viewModel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.PartialOrder2
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.Address
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.model.responses.address.AddAddressResponse
import com.example.mcommerce.model.responses.address.AddressResponse
import com.example.mcommerce.model.responses.address.CustomerAddress
import com.example.mcommerce.model.responses.orders.Order
import com.example.mcommerce.model.responses.orders.OrderElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ViewModelDummy(private val repo: Repository) : ViewModel() {
    lateinit var order: Order
    var isPayPalChoose = false
    var isPaymentApproved = false

    var orderResponse: ReceivedOrdersResponse? = null
    private val mutableDraftOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()
    private val _uiState = MutableStateFlow<ApiState<Order>>(ApiState.Loading())
    var uiState: StateFlow<ApiState<Order>> = _uiState

//    private val _isLoading = MutableLiveData(false)
//    var isLoading: LiveData<Boolean> = _isLoading

    private val _cart: MutableStateFlow<ApiState<DraftOrderRequest>> = MutableStateFlow(
        ApiState.Loading()
    )
    val cart: StateFlow<ApiState<DraftOrderRequest>> = _cart

    private val _addressState = MutableStateFlow<ApiState<List<Address>>>(ApiState.Loading())
    val addressState: StateFlow<ApiState<List<Address>>> = _addressState


    private val mutableUpdatedOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    // دي فانكشن انا هستخدمخا لما يجي الكلاينت يدوس ع زرار الدفع سواء كاش او فيزا
    fun confirmOrder(draftOrderRequest2: Order) {


        viewModelScope.launch(Dispatchers.IO) {
            repo.confirmOrder(draftOrderRequest2)
                .catch {
                    _uiState.value = ApiState.Failure(it.message.toString())
                    Log.e("it", "confirmOrder:$it ")
                }
                .collectLatest {
                    _uiState.value = ApiState.Success(it)
                    Log.e("it", "getbrands:$it ")
                }
        }
    }


    fun deleteCartItemsById(id: String) {
        viewModelScope.launch {
            repo.delCartItem(id)
        }
    }

    fun getFavoriteDraftOrder(draftOrderId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavoriteDraftOrder(draftOrderId).catch {
                mutableDraftOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.d("Moneer", "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableDraftOrderStateFlow.value = ApiState.Success(it)
                Log.d("Moneer", "success getAllDraftOrders$it")
            }
        }
    }

    fun updateFavoriteDraftOrder(
        customerId: Long,
        updateDraftOrderRequest: UpdateDraftOrderRequest
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateFavoriteDraftOrder(customerId, updateDraftOrderRequest).catch {
                mutableUpdatedOrderStateFlow.value = ApiState.Failure(it.message.toString())
                Log.e("TAG", "error updateDraftOrder: ${it.message}")
            }.collect {
                mutableUpdatedOrderStateFlow.value = ApiState.Success(it.draft_order)
                Log.i("TAG", "updateDraftOrder: $it")
            }
        }
    }

    fun getCartid(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavoriteDraftOrder(id)
                .catch {
                    _cart.value = ApiState.Failure(it.message ?: "Error")
                    Log.d("ApiState", "getCartid:$it ")
                }
                .collect {
                    _cart.value = ApiState.Success(it)
                    Log.d("ApiState", "getCartid:$it ")
                }
        }
    }


    private val _createdOrderStateFlow = MutableStateFlow<ApiState<OrderElement>>(ApiState.Loading())
    var createdOrderStateFlow: StateFlow<ApiState<OrderElement>> = _createdOrderStateFlow



    fun createOrder(partialOrder2: PartialOrder2,customerId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.createOrder(partialOrder2)
                .catch {
                    _createdOrderStateFlow.value = ApiState.Failure(it.message ?: "Error")
                    Log.d("ApiState", "getCartid:$it ")
                }
                .collect {
                    _createdOrderStateFlow.value = ApiState.Success(it)
                    Log.d("ApiState", "getCartid:$it ")
                }
        }
    }



    fun loadAddresses(customerId: Long) {
        viewModelScope.launch {
            _addressState.value = ApiState.Loading()  // Emit loading state
            try {
                Log.d("API Request", "Loading addresses for customerId: $customerId")
                val response = repo.getAddresses(customerId)
                Log.d("API Response", "Addresses received: ${response.addresses.size}")

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

}


//    fun sendEmailConfirmation(customerEmail: String, orderId: String) {
//        var subject = "Order Confirmation"
//        val message = "Dear customer,\n\nYour order #$orderId has been confirmed.\nThank you for shopping with us!"
//
//        try {
//            val props = Properties().apply {
//                put("mail.smtp.auth", "true")
//                put("mail.smtp.starttls.enable", "true")
//                put("mail.smtp.host", "smtp.gmail.com") // أو مضيف SMTP آخر
//                put("mail.smtp.port", "587")
//            }
//
//            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
//                override fun getPasswordAuthentication(): PasswordAuthentication {
//                    return PasswordAuthentication(customerEmail,orderId) // استخدم بريد المستخدم وكلمة المرور
//                }
//            })
//
//            val mimeMessage = MimeMessage(session).apply {
//                setFrom(InternetAddress(customerEmail))
//                addRecipient(Message.RecipientType.TO, InternetAddress(customerEmail))
//                subject = subject
//                setText(message)
//            }
//
//            // إرسال البريد
//            Transport.send(mimeMessage)
//            Log.i("Email", "Email sent successfully to $customerEmail")
//
//        } catch (e: MessagingException) {
//            e.printStackTrace()
//            Log.e("Email", "Error sending email: ${e.localizedMessage}")
//        }
//    }


