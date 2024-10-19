package com.example.mcommerce.model.responses.coupons

import com.example.mcommerce.model.pojos.PriceRule

data class CouponResponse(
    val price_rules: List<PriceRule>
)