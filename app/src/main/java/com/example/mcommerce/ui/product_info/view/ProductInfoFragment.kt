package com.example.mcommerce.ui.product_info.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.FragmentProductInfoBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModel
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProductInfoFragment : Fragment() {
    lateinit var binding: FragmentProductInfoBinding
    lateinit var productInfoViewModel: ProductInfoViewModel
    lateinit var productInfoViewModelFactory: ProductInfoViewModelFactory
    private val TAG = "ProductInfoFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productInfoViewModelFactory = ProductInfoViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        productInfoViewModel = ViewModelProvider(
            this,
            productInfoViewModelFactory
        ).get(ProductInfoViewModel::class.java)
        getProductInfoDetails()
    }

    fun getProductInfoDetails() {
        productInfoViewModel.getProductDetails(9728829948203)
        lifecycleScope.launch {
            productInfoViewModel.productDetailsStateFlow.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        Log.e(TAG, "getProductInfoDetails: loading")
                    }

                    is ApiState.Failure -> {
                        Log.d(TAG, "getProductInfoDetails: faillllllllllllllllllll")
                    }

                    is ApiState.Success -> {
                        Log.d(TAG, "getProductInfoDetails: Successssssssssssssssss")
                        Log.d(TAG, "getProductInfoDetails: ${it.data.get(0).images}")
                        showProductInfoDetails(it.data.get(0))
                    }
                }
            }
        }
    }

    fun showProductInfoDetails(products: Products) {
        Glide.with(this).load(products.images.get(0).src).into(binding.ivCurrentImage)
//        binding.ivCurrentImage.sete(products.image)
        binding.tvTitle.text = products.title
        Log.d(TAG, "showProductInfoDetails: ${products.options}")
//        binding.tvPrice.text = products.
    }
}