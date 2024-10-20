package com.example.mcommerce.model.pojos


data class PostDraftOrderItemModel(val draft_order : DraftOrder)
data class DraftOrderRequest(
    val draft_order: DraftOrder
)

data class DraftOrder(
    var line_items: List<LineItem>,
    val applied_discount: AppliedDiscount?,
    val customer: Customers,
    val use_customer_default_address: Boolean
)

data class LineItem(
    val id: Long?= null,
    var variant_id: Long?= null,
    val product_id: Long,
    val title: String,
    val variant_title: String?= null,
    val sku: String?= null,
    val vendor: String?= null,
    val quantity: Int?= null,
    val requires_shipping: Boolean?= null,
    val taxable: Boolean?= null,
    val gift_card: Boolean?= null,
    val fulfillment_service: String?= null,
    val grams: Int?= null,
    val tax_lines: List<Any>?= null,
    val applied_discount: Any?= null,
    val name: String?= null,
    val properties: List<Any>?= null,
    val custom: Boolean?= null,
    val price: String,
    val admin_graphql_api_id: String?= null
)

data class AppliedDiscount(
    val description: String? = null,
    val value_type: String? = null,  // Could be made into enum if all possible values are known
    val value: String? = null,      // Using String to maintain decimal precision
    val amount: String? = null,     // Using String to maintain decimal precision
    val title: String? = null
)

data class Customers(
    val id:Long
)