package com.example.mcommerce.ui.dummy.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mcommerce.Alert
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
    private lateinit var lineItem: ReceivedLineItem
    lateinit var draftOrderRequest: DraftOrderRequest
     var draftOrderRequest2: DraftOrderRequest?=null
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
        fetchCartData()
        binding.buttonComplete.setOnClickListener {
            Alert.showCustomAlertDialog(
                requireActivity(),
                "Are You Sure You want To Pay with Cash",
                positiveText = "Confirm",
                positiveClickListener = { _, _ ->
                    processOrder()
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
        lifecycleScope.launch(Dispatchers.Main) {
            draftOrderRequest2?.let { viewModel.confirmOrder(it) }
            viewModel.uiState.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> Log.d("Order", "Confirming order...")
                    is ApiState.Failure -> Log.d("Order", "Order confirmation failed: ${state.message}")
                    is ApiState.Success -> {
                        Log.d("Order", "Order confirmed successfully.")
                        updateDraftOrder()
                    }
                }
            }
        }
    }
    private fun updateDraftOrder() {
        lifecycleScope.launch {
            if (::draftOrderRequest.isInitialized && draftOrderRequest.draft_order.line_items.isNotEmpty()) {
               viewModel.getFavoriteDraftOrder(draftOrderID)
                viewModel.draftOrderStateFlow.collectLatest { state->
                    when(state)
                    {
                        is ApiState.Failure ->{}
                        is ApiState.Loading -> {}
                        is ApiState.Success -> {
                            draftOrderRequest2=DraftOrderRequest(  state.data.draft_order)

                        }
                    }

                }
                viewModel.updateFavoriteDraftOrder(
                    draftOrderID,
                    UpdateDraftOrderRequest(draftOrderRequest.draft_order)
                )
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

//    fun updateDraft(lineItem: LineItem) {
//   lifecycleScope.launch {
//            if (draftOrderRequest.draft_order.line_items.size > 1) {
//                val mutableLineItems: MutableList<LineItem> = mutableListOf()
//                draftOrderRequest.draft_order.line_items.forEach {
//                    if (it != lineItem) {
//                        mutableLineItems.add(it)
//                    }
//                }
//                draftOrderRequest.draft_order.line_items = mutableLineItems
//                viewModel.updateFavoriteDraftOrder(
//                    draftOrderID,
//                    UpdateDraftOrderRequest(draftOrderRequest.draft_order)
//                )
//                delay(500)
//                viewModel.getFavoriteDraftOrder(draftOrderID)
//            } else {
//                draftOrderRequest.draft_order.line_items.get(0).sku = "null"
//                viewModel.updateFavoriteDraftOrder(
//                    draftOrderID,
//                    UpdateDraftOrderRequest(draftOrderRequest.draft_order)
//                )
//                delay(500)
//                viewModel.getFavoriteDraftOrder(draftOrderID)
//            }
//        }
//
//
//    }
//    private fun checkCart()
//    {
//
//
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.cart.collectLatest {
//                when (it) {
//                    is ApiState.Loading -> {
//
//                    }
//
//                    is ApiState.Failure -> {
//
//                    }
//
//                    is ApiState.Success -> {
//                        if (it.data.line_items?.isNotEmpty() == true) {
//
//                        } else {
//
//                        }
//                    }
//                }
//            }
//        }
//    }


//    private fun observeOnStateFlow() {
//        lifecycleScope.launch {
//            viewModel.uiState.collect {
//                when (it) {
//                    is ApiState.Failure<*> -> {
//                        Log.d("ApiState", "Error: ${it.message}")
//                    }
//                    is ApiState.Success<*> -> {
//                        // Successfully placed order
//                        ///مستني هنا id عشان امسح من ععلب draft order
////                        viewModel.deleteCartItemsById(draftOrderID.toString())
//                        findNavController().popBackStack(R.id.navigation_home, false)
//                    }
//                    is ApiState.Loading -> {
//                        // Show loading if necessary
//                    }
//                }
//            }
//        }
//    }
//    private fun observeOnLiveData() {
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            if (isLoading) {
//                Alert.showProgressDialog(requireContext())
//            } else {
//                Alert.hideProgressDialog()
//            }
//        }
//    }
//    fun draftOrderUpdate() {
//        lifecycleScope.launch {
//            viewModel.getFavoriteDraftOrder(draftOrderID)
//            viewModel.draftOrderStateFlow.collectLatest {
//                when (it) {
//                    is ApiState.Failure -> {
//                        Log.e("TAG", "onViewCreated: faillllllllllllll")
//                    }
//
//                    is ApiState.Loading -> {}
//                    is ApiState.Success -> {
//                        val mutableList: MutableList<LineItem> = mutableListOf()
//                        draftOrderRequest = it.data
//                        it.data.draft_order.line_items.forEach {
//                            if (it.sku != "null") {
//                                mutableList.add(it)
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//    }
//    private fun clickOnCash()
//    {
//        binding.buttonComplete.setOnClickListener {
//            Alert.showCustomAlertDialog(
//                requireActivity(),
//                "Are You Sure You want To Pay with Cash",
//                positiveText = "Confirm",
//                positiveClickListener = { _, _ ->
//                    viewModel.getCartid(draftOrderID.toString())
//                    viewModel.confirmOrder()
//                    updateDraft(lineItem)
//                },
//                negText = "Cancel",
//                negClickListener = {_,_->
//                    Alert.dismissAlertDialog()
//                }
//            )
//        }
//    }

}
