package com.example.mcommerce.ui.placedOrder.view

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
import com.example.mcommerce.databinding.FragmentPlacedOrderBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.ui.placedOrder.viewModel.PlaceViewModelFactory
import com.example.mcommerce.ui.placedOrder.viewModel.ViewModelPlaceOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

const val MAXIMUM_CASH_AMOUNT = 10000f

class PlacedOrderFragment : Fragment() {


    private lateinit var binding: FragmentPlacedOrderBinding
    private lateinit var viewModel: ViewModelPlaceOrder
    private lateinit var viewModelFactory: PlaceViewModelFactory


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlacedOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = PlaceViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModelPlaceOrder::class.java)


        viewModel.confirmOrder()


        observeOnStateFlow()
        observeOnLiveData()
        checkCart()
        clickOnCash()
    }




    private fun clickOnCash()
    {
        binding.placeorderid.setOnClickListener {
            Alert.showCustomAlertDialog(
                requireActivity(),
                "Are You Sure You want To Pay with Cash",
                positiveText = "Confirm",
                positiveClickListener = { _, _ ->
                    viewModel.confirmOrder()
                },
                negText = "Cancel",
                negClickListener = {_,_->
                    Alert.dismissAlertDialog()
                }
            )
        }
    }

    private fun observeOnLiveData() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                Alert.showProgressDialog(requireContext())
            } else {
                Alert.hideProgressDialog()
            }
        }
    }

    private fun observeOnStateFlow() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    is ApiState.Failure<*> -> {
                        Log.d("ApiState", "Error: ${it.message}")
                    }
                    is ApiState.Success<*> -> {
                        // Successfully placed order
                        ///مستني هنا id عشان امسح من ععلب draft order
//                        viewModel.deleteCartItemsById()
                        findNavController().popBackStack(R.id.navigation_home, false)
                    }
                    is ApiState.Loading -> {
                        // Show loading if necessary
                    }
                }
            }
        }
    }

    private fun checkCart() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.cart.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        // Show loading if necessary
                    }
                    is ApiState.Failure -> {
                        Log.d("ApiState", "Error fetching cart: ${it.message}")
                    }
                    is ApiState.Success -> {
                        if (it.data.line_items?.isEmpty() == true) {
//                            initializeViewModelVariables()
                        } else {
                            // Handle empty cart case
                        }
                    }
                }
            }
        }
    }

//    private fun initializeViewModelVariables() {
//        viewModel.maximumCashAmount = MAXIMUM_CASH_AMOUNT.toString()
//        viewModel.isPayPalChoose = isPayPalChoose
//        viewModel.isPaymentApproved = isPaymentApproved
//        viewModel.orderResponse = Order(listOf(getOrderItem()) )
//    }
//
//    private fun getOrderItem(): OrderElement {
//        val discountedPrice = if (discountPrice.toFloat() >= 50.0f)
//            discountPrice.toFloat().minus(50.0f).toString()
//        else
//            "0"
//
//        return OrderElement(
//            lineItems = viewModel.lineItemsList,
//            totalPrice = totalPrice,
//            totalTax = discountedPrice,
//            shippingAddress = getShippingAddress()
//        )
//    }

//    private fun getShippingAddress(): Addresss {
//        return Addresss(
//            country = address.country,
//            address1 = address.address1,
//            address2 = address.address2,
//            city = address.city,
//            countryCode = address.country_code ?: "", // Handle nullability if needed
//            firstName = address.first_name ?: "",    // Provide default value if null
//            lastName = address.last_name ?: "",      // Provide default value if null
//            name = address.name ?: "",              // Provide default value if null
//            phone = address.phone ?: "",            // Provide default value if null
//            zip = address.zip ?: ""                 // Provide default value if null
//        )
//    }

}
