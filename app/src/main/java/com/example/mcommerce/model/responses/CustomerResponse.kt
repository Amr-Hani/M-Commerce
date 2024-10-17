package com.example.mcommerce.model.responses

data class CustomerResponse(
    val customers: List<Customer>
)

data class Customer(
    val id: Long,
    val email: String,
    val created_at: String,
    val updated_at: String,
    val first_name: String?,
    val last_name: String?,
    val orders_count: Int,
    val state: String,
    val total_spent: String,
    val last_order_id: Long?,
    val note: String?,
    val verified_email: Boolean,
    val multipass_identifier: String?,
    val tax_exempt: Boolean,
    val tags: String?,
    val last_order_name: String?,
    val currency: String,
    val phone: String?,

    val addresses: List<Address>,
    val tax_exemptions: List<Any>,
    val email_marketing_consent: EmailMarketingConsent,
    val sms_marketing_consent: SmsMarketingConsent,
    val admin_graphql_api_id: String,
    val default_address: Address
)

data class Address(
    val id: Long,
    val customer_id: Long,
    val first_name: String?,
    val last_name: String?,
    val company: String?,
    val address1: String,
    val address2: String?,
    val city: String,
    val province: String,
    val country: String,
    val zip: String,
    val phone: String?,
    val name: String?,
    val province_code: String,
    val country_code: String,
    val country_name: String,
    val default: Boolean
)

data class EmailMarketingConsent(
    val state: String,
    val opt_in_level: String?,
    val consent_updated_at: String?
)

data class SmsMarketingConsent(
    val state: String,
    val opt_in_level: String?,
    val consent_updated_at: String?,
    val consent_collected_from: String
)


//
//data class CustomerResponse(
//	val customer: Customer? = null
//)
//
//data class AddressesItem(
//	val zip: String? = null,
//	val country: String? = null,
//	val address2: Any? = null,
//	val city: String? = null,
//	val address1: String? = null,
//	val lastName: String? = null,
//	val provinceCode: String? = null,
//	val countryCode: String? = null,
//	val jsonMemberDefault: Boolean? = null,
//	val province: String? = null,
//	val phone: String? = null,
//	val name: String? = null,
//	val countryName: String? = null,
//	val company: Any? = null,
//	val id: Int? = null,
//	val customerId: Int? = null,
//	val firstName: String? = null
//)
//
//data class SmsMarketingConsent(
//	val consentUpdatedAt: Any? = null,
//	val consentCollectedFrom: String? = null,
//	val state: String? = null,
//	val optInLevel: String? = null
//)
//
//data class Customer(
//	val totalSpent: String? = null,
//	val note: Any? = null,
//	val addresses: List<AddressesItem?>? = null,
//	val lastOrderName: Any? = null,
//	val lastOrderId: Any? = null,
//	val taxExempt: Boolean? = null,
//	val emailMarketingConsent: EmailMarketingConsent? = null,
//	val createdAt: String? = null,
//	val lastName: String? = null,
//	val multipassIdentifier: Any? = null,
//	val verifiedEmail: Boolean? = null,
//	val tags: String? = null,
//	val ordersCount: Int? = null,
//	val smsMarketingConsent: SmsMarketingConsent? = null,
//	val defaultAddress: DefaultAddress? = null,
//	val updatedAt: String? = null,
//	val phone: String? = null,
//	val adminGraphqlApiId: String? = null,
//	val taxExemptions: List<Any?>? = null,
//	val currency: String? = null,
//	val id: Int? = null,
//	val state: String? = null,
//	val firstName: String? = null,
//	var email: String? = null
//)
//
//data class DefaultAddress(
//	val zip: String? = null,
//	val country: String? = null,
//	val address2: Any? = null,
//	val city: String? = null,
//	val address1: String? = null,
//	val lastName: String? = null,
//	val provinceCode: String? = null,
//	val countryCode: String? = null,
//	val jsonMemberDefault: Boolean? = null,
//	val province: String? = null,
//	val phone: String? = null,
//	val name: String? = null,
//	val countryName: String? = null,
//	val company: Any? = null,
//	val id: Int? = null,
//	val customerId: Int? = null,
//	val firstName: String? = null
//)
//
//data class EmailMarketingConsent(
//	val consentUpdatedAt: Any? = null,
//	val state: String? = null,
//	val optInLevel: String? = null
//)

