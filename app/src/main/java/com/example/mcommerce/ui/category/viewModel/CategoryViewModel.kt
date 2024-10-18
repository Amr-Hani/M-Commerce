package com.example.mcommerce.ui.category.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CategoryItem
import com.example.mcommerce.model.pojos.CategoryPOJO
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.responses.ProductResponse

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.log

class CategoryViewModel(private val repo: Repository) : ViewModel() {
    private val _category = MutableStateFlow<ApiState<List<CustomCollection>>>(ApiState.Loading())
    val category = _category.asStateFlow()

    fun getCategory(categoryType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // استدعاء الدالة الخاصة بك لجلب البيانات بناءً على النوع
            repo.getProductsBySubCategory(categoryType)
                .catch { exception ->
                    _category.value = ApiState.Failure(exception.message.toString())
                    Log.e("TAG", "Error fetching category: ${exception.message}")
                }
                .collect { products ->
                    _category.value = ApiState.Success(products)
                    Log.d("products", "getCategory: $products")
                }
        }
    }


}


//    private val _categoryDetails = MutableStateFlow<ApiState<List<ProductResponse>>>(ApiState.Loading)
//    val categoryDetails = _category.asStateFlow()
//
//    fun getProductCategoryById(id:Long){
//        viewModelScope.launch(Dispatchers.IO) {
//            // استدعاء الدالة الخاصة بك لجلب البيانات بناءً على النوع
//            repo.getProductById(id)
//                .catch { exception ->
//                    _categoryDetails.value = ApiState.OnFailed(exception)
//                    Log.e("TAG", "Error fetching category: ${exception.message}")
//                }
//                .collect { products ->
//                    _categoryDetails.value = ApiState.OnSuccess(products)
//                }
//        }
//    }





//    fun getProductsBySubCategory(mainCategory: String, subCategory: String) {
//        val productsOfCategory: MutableList<Products> = mutableListOf()
//
//        viewModelScope.launch {
//            _category.value = ApiState.Loading
//
//            // Fetch all products (or favorite products if implemented)
////            val favoriteProducts = repo.getProductDetails(subCategory)
//
//            // Fetch products by subcategory
//            repo.getProductsBySubCategory(subCategory)
//                .catch { error ->
//                    _category.value = ApiState.OnFailed(error)
//                }
//                .collect { products ->
//
//                    products.forEach { product ->
//                        filterProductByMainCategory(product, mainCategory, productsOfCategory)
//                    }
//
//                    // Set price for each product
//                    getPriceForEachProduct(productsOfCategory)
//
//                    // Add favorite products (optional: if needed in the future)
//                    val productsWithFavorites = productsOfCategory.map { product ->
//                        // Uncomment the block below if you implement favorite functionality
////                        if (favoriteProducts.any { it.id == product.id }) {
////                            product.copy(isFavorited = true)
////                        } else {
//                        product
////                        }
//                    }
//
//                    // Update the state with the final product list
//                    _category.value = ApiState.OnSuccess(productsWithFavorites)
//                }
//        }
//    }
//
//    private fun filterProductByMainCategory(
//        product: Products,
//        mainCategory: String,
//        productsOfCategory: MutableList<Products>
//    ) {
//        if (product.tags?.contains(mainCategory) == true) {
//            productsOfCategory.add(product)
//            Log.e("TAG", "Product found for main category: $mainCategory")
//        } else {
//            Log.e("TAG", "Product not found for main category: $mainCategory")
//        }
//    }
//
//    private fun getPriceForEachProduct(products: List<Products>) {
//        products.forEach { item ->
//            if (item.variants?.isNotEmpty() == true) {
//                // Set the price to the first variant's price
//                item.variants[0].price = item.variants[0].price
//            }
//        }
//    }
//}
