package com.example.mcommerce.model.pojos


import com.google.gson.annotations.SerializedName



data class CategoryPOJO (
    @SerializedName("custom_collections")
    val customCollections: List<CustomCollection>
)


data class CustomCollection (
    val id: Long,
    val handle: String,
    val title: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("body_html")
    val bodyHTML: String? = null,

    @SerializedName("published_at")
    val publishedAt: String,

    @SerializedName("sort_order")
    val sortOrder: String,

    @SerializedName("template_suffix")
    val templateSuffix: String? = null,  // Changed from JsonElement? to String?

    @SerializedName("published_scope")
    val publishedScope: String,

    @SerializedName("admin_graphql_api_id")
    val adminGraphqlAPIID: String,

    val image: Image? = null
)


data class Imag (
    @SerializedName("created_at")
    val createdAt: String,

    val alt: String? = null,  // Changed from JsonElement? to String?
    val width: Long,
    val height: Long,
    val src: String
)
