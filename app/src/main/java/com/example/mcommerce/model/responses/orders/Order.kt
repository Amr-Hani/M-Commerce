package com.example.mcommerce.model.responses.orders



data class Order (
    val orders: OrderElement

)

    data class OrderElement(
        val id: Long,
        val adminGraphqlAPIID: String,
        val appID: Long,
        val browserIP: Any? = null,
        val buyerAcceptsMarketing: Boolean,
        val cancelReason: Any? = null,
        val cancelledAt: Any? = null,
        val cartToken: Any? = null,
        val checkoutID: Any? = null,
        val checkoutToken: Any? = null,
        val clientDetails: Any? = null,
        val closedAt: Any? = null,
        val company: Any? = null,
        val confirmationNumber: String,
        val confirmed: Boolean,
        val contactEmail: String,
        val createdAt: String,
        val currency: Currency,
        val currentSubtotalPrice: String,
        val currentSubtotalPriceSet: Set,
        val currentTotalAdditionalFeesSet: Any? = null,
        val currentTotalDiscounts: String,
        val currentTotalDiscountsSet: Set,
        val currentTotalDutiesSet: Any? = null,
        val currentTotalPrice: String,
        val currentTotalPriceSet: Set,
        val currentTotalTax: String,
        val currentTotalTaxSet: Set,
        val customerLocale: Any? = null,
        val deviceID: Any? = null,
        val discountCodes: List<Any?>,
        val dutiesIncluded: Boolean,
        val email: String,
        val estimatedTaxes: Boolean,
        val financialStatus: FinancialStatus,
        val fulfillmentStatus: Any? = null,
        val landingSite: Any? = null,
        val landingSiteRef: Any? = null,
        val locationID: Any? = null,
        val merchantBusinessEntityID: MerchantBusinessEntityID,
        val merchantOfRecordAppID: Any? = null,
        val name: String,
        val note: Any? = null,
        val noteAttributes: List<Any?>,
        val number: Long,
        val orderNumber: Long,
        val orderStatusURL: String,
        val originalTotalAdditionalFeesSet: Any? = null,
        val originalTotalDutiesSet: Any? = null,
        val paymentGatewayNames: List<Any?>,
        val phone: Any? = null,
        val poNumber: Any? = null,
        val presentmentCurrency: Currency,
        val processedAt: String,
        val reference: Any? = null,
        val referringSite: Any? = null,
        val sourceIdentifier: Any? = null,
        val sourceName: String,
        val sourceURL: Any? = null,
        val subtotalPrice: String,
        val subtotalPriceSet: Set,
        val tags: Tags,
        val taxExempt: Boolean,
        val taxLines: List<Any?>,
        val taxesIncluded: Boolean,
        val test: Boolean,
        val token: String,
        val totalCashRoundingPaymentAdjustmentSet: Set,
        val totalCashRoundingRefundAdjustmentSet: Set,
        val totalDiscounts: String,
        val totalDiscountsSet: Set,
        val totalLineItemsPrice: String,
        val totalLineItemsPriceSet: Set,
        val totalOutstanding: String,
        val totalPrice: String,
        val totalPriceSet: Set,
        val totalShippingPriceSet: Set,
        val totalTax: String,
        val totalTaxSet: Set,
        val totalTipReceived: String,
        val totalWeight: Long,
        val updatedAt: String,
        val userID: Any? = null,
        val billingAddress: Address,
        val customer: Customer,
        val discountApplications: List<Any?>,
        val fulfillments: List<Any?>,
        val lineItems: MutableList<LineItem>,
        val paymentTerms: Any? = null,
        val refunds: List<Any?>,
        val shippingAddress: Address,
        val shippingLines: List<Any?>
    )

    data class Address(
        val firstName: String,
        val address1: String,
        val phone: String,
        val city: String,
        val zip: String,
        val province: String? = null,
        val country: String,
        val lastName: String,
        val address2: String? = null,
        val company: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val name: String,
        val countryCode: String,
        val provinceCode: Any? = null,
        val id: Long? = null,
        val customerID: Long? = null,
        val countryName: String? = null,
        val default: Boolean? = null
    )

    enum class Currency {
        EGP
    }

    data class Set(
        val shopMoney: Money,
        val presentmentMoney: Money
    )

    data class Money(
        val amount: String,
        val currencyCode: Currency
    )

    data class Customer(
        val id: Long,
        val email: String,
        val createdAt: String,
        val updatedAt: String,
        val firstName: String,
        val lastName: String,
        val state: CustomerState,
        val note: Any? = null,
        val verifiedEmail: Boolean,
        val multipassIdentifier: Any? = null,
        val taxExempt: Boolean,
        val phone: String,
        val emailMarketingConsent: MarketingConsent,
        val smsMarketingConsent: MarketingConsent,
        val tags: Tags,
        val currency: Currency,
        val taxExemptions: List<Any?>,
        val adminGraphqlAPIID: String,
        val defaultAddress: Address
    )

    data class MarketingConsent(
        val state: EmailMarketingConsentState,
        val optInLevel: OptInLevel,
        val consentUpdatedAt: Any? = null,
        val consentCollectedFrom: ConsentCollectedFrom? = null
    )

    enum class ConsentCollectedFrom {
        Other
    }

    enum class OptInLevel {
        SingleOptIn
    }

    enum class EmailMarketingConsentState {
        NotSubscribed
    }

    enum class CustomerState {
        Disabled
    }

    enum class Tags {
        EgnitionSampleData,
        EgnitionSampleDataReferral,
        EgnitionSampleDataVIP
    }

    enum class FinancialStatus {
        Paid,
        Pending,
        Refunded
    }

    data class LineItem(
        val id: Long,
        val adminGraphqlAPIID: String,
        val attributedStaffs: List<Any?>,
        val currentQuantity: Long,
        val fulfillableQuantity: Long,
        val fulfillmentService: FulfillmentService,
        val fulfillmentStatus: Any? = null,
        val giftCard: Boolean,
        val grams: Long,
        val name: String,
        val price: String,
        val priceSet: Set,
        val productExists: Boolean,
        val productID: Long,
        val properties: List<Any?>,
        val quantity: Long,
        val requiresShipping: Boolean,
        val sku: String,
        val taxable: Boolean,
        val title: String,
        val totalDiscount: String,
        val totalDiscountSet: Set,
        val variantID: Long,
        val variantInventoryManagement: VariantInventoryManagement,
        val variantTitle: String,
        val vendor: String,
        val taxLines: List<Any?>,
        val duties: List<Any?>,
        val discountAllocations: List<Any?>
    )

    enum class FulfillmentService {
        Manual
    }

    enum class VariantInventoryManagement {
        Shopify
    }

    enum class MerchantBusinessEntityID {
        MTkwNDYwNzUwMTIz
    }
