package com.example.mcommerce.ui.dummy.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.orders.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ViewModelDummy(private val repo: Repository) : ViewModel() {

    var isPayPalChoose = false
    var isPaymentApproved = false
    var orderResponse: Order? = null
    private val mutableDraftOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrderRequest>>(ApiState.Loading())
    val draftOrderStateFlow = mutableDraftOrderStateFlow.asStateFlow()
    private val _uiState = MutableStateFlow<ApiState<Order>>(ApiState.Loading())
    var uiState: StateFlow<ApiState<Order>> = _uiState

    private val _isLoading = MutableLiveData(false)
    var isLoading: LiveData<Boolean> = _isLoading

    private val _cart: MutableStateFlow<ApiState<ReceivedDraftOrder>> = MutableStateFlow(
        ApiState.Loading()
    )
    val cart: StateFlow<ApiState<ReceivedDraftOrder>> = _cart
    private val mutableUpdatedOrderStateFlow =
        MutableStateFlow<ApiState<DraftOrder>>(ApiState.Loading())
    val updatedOrderStateFlow = mutableUpdatedOrderStateFlow.asStateFlow()

    // دي فانكشن انا هستخدمخا لما يجي الكلاينت يدوس ع زرار الدفع سواء كاش او فيزا
    fun confirmOrder() {
        viewModelScope.launch {
            _isLoading.value = true
            _uiState.value = ApiState.Loading()

            if ((isPaymentApproved && isPayPalChoose) || !isPayPalChoose) {
                Log.i("TAG", "confirmOrder: $orderResponse")
                orderResponse?.let { order ->
                    repo.confirmOrder(order).catch { e ->
                        _isLoading.value = false
                        _uiState.value = e.localizedMessage?.let {
                            ApiState.Failure("server down, please try again later: $it")
                        }!!
                    }.collect { orderItems ->
                        _isLoading.value = false
                        _uiState.value = ApiState.Success(orderItems)

                        // إرسال البريد الإلكتروني بعد تأكيد الطلب
                        val customerEmail = order.orders.customer.email // افترض أن بريد العميل موجود في orderResponse
                        val orderId = orderItems.orders.id // يمكنك استخدام معرّف الطلب أو أي بيانات أخرى

                        if (customerEmail != null) {
                            sendEmailConfirmation(customerEmail, orderId.toString())
                        }
                    }
                }
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
                Log.e("TAG", "error getAllDraftOrders: ${it.message}")
            }.collect {
                mutableDraftOrderStateFlow.value = ApiState.Success(it)
                Log.i("TAG", "success getAllDraftOrders")
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

    fun getCartid(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getDraftOrderById(id)
                .catch {
                    _cart.value = ApiState.Failure(it.message ?: "Error")
                }
                .collect {
                    _cart.value = ApiState.Success(it)
                }
        }
    }

    fun sendEmailConfirmation(customerEmail: String, orderId: String) {
        var subject = "Order Confirmation"
        val message = "Dear customer,\n\nYour order #$orderId has been confirmed.\nThank you for shopping with us!"

        try {
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com") // أو مضيف SMTP آخر
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(customerEmail,orderId) // استخدم بريد المستخدم وكلمة المرور
                }
            })

            val mimeMessage = MimeMessage(session).apply {
                setFrom(InternetAddress(customerEmail))
                addRecipient(Message.RecipientType.TO, InternetAddress(customerEmail))
                subject = subject
                setText(message)
            }

            // إرسال البريد
            Transport.send(mimeMessage)
            Log.i("Email", "Email sent successfully to $customerEmail")

        } catch (e: MessagingException) {
            e.printStackTrace()
            Log.e("Email", "Error sending email: ${e.localizedMessage}")
        }
    }


}
