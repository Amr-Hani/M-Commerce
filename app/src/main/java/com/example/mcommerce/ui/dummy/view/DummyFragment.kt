package com.example.mcommerce.ui.dummy.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mcommerce.Alert
import com.example.mcommerce.OrderAddress
import com.example.mcommerce.OrderAppliedDiscount
import com.example.mcommerce.OrderCustomer
import com.example.mcommerce.OrderLineItem
import com.example.mcommerce.PartialOrder
import com.example.mcommerce.PartialOrder2
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentDummyBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.model.responses.Address
import com.example.mcommerce.model.responses.ReceivedLineItem
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.dummy.viewModel.DummyViewModelFactory
import com.example.mcommerce.ui.dummy.viewModel.ViewModelDummy
import com.example.mcommerce.ui.home.viewModel.HomeViewFactory
import com.example.mcommerce.ui.home.viewModel.HomeViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class DummyFragment : Fragment() {

    private lateinit var binding: FragmentDummyBinding
    private lateinit var viewModel: ViewModelDummy
    private lateinit var viewModelfactory: DummyViewModelFactory
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewFactory: HomeViewFactory
    private var totalPrice by Delegates.notNull<Double>()
    private var discountPrice by Delegates.notNull<Double>()
    private lateinit var sharedPreferences: SharedPreferences
    private var draftOrderID: Long = 0
    private var customerID: Long = 0
    private lateinit var lineItem: ReceivedLineItem
    lateinit var draftOrderRequest: DraftOrderRequest
    var draftOrderRequest2: DraftOrderRequest? = null
    lateinit var address: Address


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDummyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelfactory = DummyViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        viewModel = ViewModelProvider(this, viewModelfactory).get(ViewModelDummy::class.java)

        totalPrice = arguments?.getDouble("totalPrice") ?: 0.0
        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        draftOrderID = (sharedPreferences.getString(MyKey.MY_CARD_DRAFT_ID, "1")
            ?: "1").toLong()
        binding.textViewTotalPrice.text = "Total Price: $$totalPrice"
        customerID = (sharedPreferences.getString(MyKey.MY_CUSTOMER_ID, "8246104654123")
            ?: "8246104654123").toLong()
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
        getAddress(customerID)
        // Verify discount code button click
        binding.buttonVerifyCode.setOnClickListener {
            applyDiscount()

        }
        fetchCartData()
        setupObservers()
        updateDraftOrder()
        binding.buttonComplete.setOnClickListener {
            Alert.showCustomAlertDialog(
                requireActivity(),
                "Are You Sure You want To Pay with Cash",
                positiveText = "Confirm",
                positiveClickListener = { _, _ ->
                    processOrder()
                    createdOrder()
                },
                negText = "Cancel",
                negClickListener = { _, _ -> Alert.dismissAlertDialog() }
            )
        }
//        checkCart()
//        observeOnLiveData()
//        observeOnStateFlow()
//        clickOnCash()
//        viewModel.getCartid(draftOrderID.toString())
////        viewModel.deleteCartItemsById(draftOrderID.toString())
//        draftOrderUpdate()
//      viewModel.confirmOrder()
    }

    private fun fetchCartData() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getCartid(draftOrderID)
            viewModel.cart.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> Log.d("Cart", "Fetching cart data...")
                    is ApiState.Failure -> Log.e("Cart", "Error: ${state.message}")
                    is ApiState.Success -> {
                        if (state.data.draft_order.line_items?.isNotEmpty() == true) {
                            Log.d("Cart", "Cart data fetched successfully.")
//                            lineItem = state.data.draft_order.line_items// Just an example
                        } else {
                            Log.d("Cart", "Cart is empty.")
                        }
                    }
                }
            }
        }
    }

    private fun processOrder() {
//        lifecycleScope.launch(Dispatchers.Main) {
//            draftOrderRequest2?.let { viewModel.confirmOrder(it) }
//            viewModel.uiState.collectLatest { state ->
//                when (state) {
//                    is ApiState.Loading -> Log.d("Order", "Confirming order...")
//                    is ApiState.Failure -> Log.d("Order", "Order confirmation failed: ${state.message}")
//                    is ApiState.Success -> {
//                        Log.d("Order", "Order confirmed successfully.")
//                        updateDraftOrder()
//                    }
//                }
//            }
//        }
    }

    private fun updateDraftOrder() {
        lifecycleScope.launch {
            viewModel.getFavoriteDraftOrder(draftOrderID)
            viewModel.draftOrderStateFlow.collectLatest { state ->
                when (state) {
                    is ApiState.Failure -> {}
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        draftOrderRequest2 = DraftOrderRequest(state.data.draft_order)
                        Log.d("TAG", "updateDraftOrder: $draftOrderRequest2")
                    }
                }

            }
            viewModel.updateFavoriteDraftOrder(
                draftOrderID,
                UpdateDraftOrderRequest(draftOrderRequest.draft_order)
            )
            if (::draftOrderRequest.isInitialized && draftOrderRequest.draft_order.line_items.isNotEmpty()) {

                Log.d("Update", "Draft order updated.")
            } else {
                Log.d("Update", "No items to update in draft order.")
            }
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
                                Log.i(
                                    "Coupon Applied",
                                    "Fixed amount discount applied: -$discountAmount. New total: $totalPrice"
                                )
                            } else if (value.value_type == "percentage") {
                                val discountPercentage = value.value.toDoubleOrNull() ?: 0.0
                                val discountAmount = totalPrice * (discountPercentage / 100)
                                totalPrice += discountAmount
                                Log.i(
                                    "Coupon Applied",
                                    "Percentage discount applied: -$discountAmount. New total: $totalPrice"
                                )
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

    fun getAddress(customerId: Long) {
        viewModel.loadAddresses(customerId)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.addressState.collect { uiState ->
                when (uiState) {
                    is ApiState.Loading -> {
                    }

                    is ApiState.Success<*> -> {

                        val addresses = uiState.data as List<Address>
                        address = addresses.get(0)
                        Log.d("setupObservers", "setupObservers: $addresses")

                    }

                    is ApiState.Failure -> {
                        Log.d("setupObservers", "setupObservers:${uiState.message} ")
                        Toast.makeText(
                            requireContext(),
                            "Error: ${uiState.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun createdOrder() {
        val dummyOrder = PartialOrder2(
            order = PartialOrder(
                customer = OrderCustomer(
                    id = customerID
                ),
                line_items = listOf(
                    OrderLineItem(
                        variant_id = draftOrderRequest2?.draft_order?.line_items?.get(0)?.variant_id,
                        quantity = draftOrderRequest2?.draft_order?.line_items?.get(0)?.quantity,
                        name = draftOrderRequest2?.draft_order?.line_items?.get(0)?.name.toString(),
                        title = draftOrderRequest2?.draft_order?.line_items?.get(0)?.title.toString(),
                        price = totalPrice.toString()
                    )
                ),
                billing_address = OrderAddress(
                    first_name = address.first_name,
                    last_name = address.last_name,
                    address1 = address.address1,
                    address2 = address.address2,
                    city = address.city,
                    province = address.province,
                    country = address.country,
                    zip = address.zip,
                    phone = address.phone
                ),
                shipping_address = OrderAddress(
                    first_name = address.first_name,
                    last_name = address.last_name,
                    address1 = address.address1,
                    address2 = address.address2,
                    city = address.city,
                    province = address.province,
                    country = address.country,
                    zip = address.zip,
                    phone = address.phone
                )
            )
        )
        Log.d("TAG", "createdOrder: $dummyOrder")
        dummyOrder
        viewModel.createOrder(dummyOrder,customerID)
        lifecycleScope.launch {
            viewModel.createdOrderStateFlow.collectLatest {
                when(it){
                    is ApiState.Failure -> {
                        Log.d("TAG", "createdOrder: ${it.message}")
                    }
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        Log.d("TAG", "createdOrder: ${it.data}")

                    }
                }
            }
        }

    }


}
