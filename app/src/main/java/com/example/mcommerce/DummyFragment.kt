package com.example.mcommerce

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mcommerce.databinding.FragmentDummyBinding
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.ui.home.viewModel.HomeViewFactory
import com.example.mcommerce.ui.home.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class DummyFragment : Fragment() {

    private lateinit var binding: FragmentDummyBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewFactory: HomeViewFactory
    private var totalPrice by Delegates.notNull<Double>()
    private var discountPrice by Delegates.notNull<Double>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDummyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        totalPrice = arguments?.getDouble("totalPrice") ?: 0.0
        binding.textViewTotalPrice.text = "Total Price: $$totalPrice"

        // Initialize ViewModel with the factory
        homeViewFactory = HomeViewFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        homeViewModel = ViewModelProvider(
            this,
            homeViewFactory
        ).get(HomeViewModel::class.java)

        // Fetch coupons
        homeViewModel.getCoupons()
        observeCoupons()

        // Verify discount code button click
        binding.buttonVerifyCode.setOnClickListener {
            applyDiscount()

        }
    }

    private fun observeCoupons() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.coupons.collectLatest { couponMap ->
                if (couponMap.isNotEmpty()) {
                    Log.i("Coupons", "Fetched coupons: $couponMap")
                }
            }
        }
    }

    private fun applyDiscount() {
        val discountCode = binding.editTextDiscountCode.text.toString()
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.coupons.collectLatest { couponMap ->
                if (couponMap.isNotEmpty()) {
                    couponMap.forEach { (key, value) ->
                        Log.i("couponMap", "Coupon: $value ")
                        if (key == discountCode) {
                            binding.buttonVerifyCode.visibility = View.GONE
                            if (value.value_type == "fixed_amount") {
                                val discountAmount = value.value.toDoubleOrNull() ?: 0.0
                                totalPrice += discountAmount
                                Log.i("Coupon Applied", "Fixed amount discount applied: -$discountAmount. New total: $totalPrice")
                            } else if (value.value_type == "percentage") {
                                val discountPercentage = value.value.toDoubleOrNull() ?: 0.0
                                val discountAmount = totalPrice * (discountPercentage / 100)
                                totalPrice += discountAmount
                                Log.i("Coupon Applied", "Percentage discount applied: -$discountAmount. New total: $totalPrice")
                            }

                            // Update the final price TextView
                            binding.textViewFinalPrice.text = "Price after Discount: $$totalPrice"
                            return@collectLatest // Exit the loop once discount is applied
                        }
                    }
                }
            }
        }
    }
}
