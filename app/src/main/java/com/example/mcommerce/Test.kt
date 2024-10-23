package com.example.mcommerce

data class PartialOrder2(
    val order:PartialOrder
)
data class PartialOrder(
    var customer: OrderCustomer? = null, //must
    var line_items: List<OrderLineItem>? = null, //must
    var applied_discount: OrderAppliedDiscount? = null, //
    var billing_address: OrderAddress? = null, //must
    var shipping_address: OrderAddress? = null, // must
    var fulfillment_status: String? = null, //
    var payment_gateway_names: List<String>? = null //
)

data class OrderCustomer(
    val id: Long?
)

data class OrderLineItem(
    var variant_id: Long?,
    var quantity: Int?,
    var name: String?,
    var title: String?,
    var price: String?
)

data class OrderAppliedDiscount(
    val description: String?,
    val value: String?,
    val value_type: String?,
    val amount: String?,
    val title: String?
)

data class OrderAddress(
    val first_name: String?,
    val last_name: String?,
    val address1: String?,
    val address2: String?,
    val city: String?,
    val province: String?,
    val country: String?,
    val zip: String?,
    val phone:String?
)

//
//class SharedOrderViewModel : ViewModel() {
//    private val _partialOrder = MutableStateFlow(PartialOrder())
//    val partialOrder: StateFlow<PartialOrder> = _partialOrder
//
//    fun updateCustomer(customer: OrderCustomer) {
//        _partialOrder.value = _partialOrder.value.copy(customer = customer)
//    }
//
//    fun updateLineItems(lineItems: List<OrderLineItem>) {
//        _partialOrder.value = _partialOrder.value.copy(line_items = lineItems)
//    }
//
//    fun updateAppliedDiscount(appliedDiscount: OrderAppliedDiscount) {
//        _partialOrder.value = _partialOrder.value.copy(applied_discount = appliedDiscount)
//    }
//
//    fun updateBillingAddress(billingAddress: OrderAddress) {
//        _partialOrder.value = _partialOrder.value.copy(billing_address = billingAddress)
//    }
//
//    fun updateShippingAddress(shippingAddress: OrderAddress) {
//        _partialOrder.value = _partialOrder.value.copy(shipping_address = shippingAddress)
//    }
//
//    fun updatePaymentGateway(paymentGatewayNames: List<String>) {
//        _partialOrder.value = _partialOrder.value.copy(payment_gateway_names = paymentGatewayNames)
//        }
//}