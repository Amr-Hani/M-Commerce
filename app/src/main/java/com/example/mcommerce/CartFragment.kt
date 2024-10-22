package com.example.mcommerce

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcommerce.databinding.FragmentCartBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.network.currency.RetrofitInstance
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.cart.view.CartAdapter
import com.example.mcommerce.ui.cart.view.payment.PaymentDialogFragment
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModel
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModelFactory
import com.example.mcommerceapp.model.network.RemoteDataSourceForCurrency
import com.example.mcommerceapp.model.network.Repo
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(), PaymentDialogFragment.CurrencySelectionListener {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val productInfoViewModel: ProductInfoViewModel by viewModels {
        ProductInfoViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
    }

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
    }

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService)),
            Repo.getInstance(RemoteDataSourceForCurrency(RetrofitInstance.exchangeRateApi))
        )
    }

    private lateinit var cartAdapter: CartAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var draftOrderRequest: DraftOrderRequest
    private var draftOrderID: Long = 0
    private lateinit var productList: List<LineItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences = requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        sharedPreferences2 =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        Log.d("draftOrderID", "draftOrderID before  creation : $draftOrderID")
        draftOrderID = sharedPreferences2.getString(MyKey.MY_CARD_DRAFT_ID, "1")?.toLongOrNull() ?: 1L
        Log.d("draftOrderID", "draftOrderID after  creation : $draftOrderID")
        setupRecyclerView()
        observeDraftOrder()
        observeExchangeRates() // Start observing exchange rates

        settingViewModel.fetchLatestRates()  //do not forget to uncomment before show

        binding.btnCheckout.setOnClickListener { handleCheckout() }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onItemChanged = { updateSubtotal() },
            onItemDeleted = { product -> deleteProduct(product) }
        )
        binding.recyclerViewCart.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        updateSubtotal()
    }

    private fun observeDraftOrder() {
        viewLifecycleOwner.lifecycleScope.launch {
            productInfoViewModel.getDraftOrder(draftOrderID)
            Log.d("draftOrderID", "draftOrderID after  use getDraft : $draftOrderID")
            productInfoViewModel.draftOrderStateFlow.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> { }
                    is ApiState.Success -> {
                        val mutableList: MutableList<LineItem> = mutableListOf()

                        state.data.draft_order.line_items.forEach {
                            if (it.sku != "null") {
                                mutableList.add(it)
                            }
                        }
                        productList = state.data.draft_order.line_items
                        draftOrderRequest = state.data
                        cartAdapter.submitList(mutableList)
                        updateSubtotal()
                        Log.d("draftOrderID", "productListl : $productList")
                        Log.d("draftOrderID", "draftOrderID after  updateSubtotal : $draftOrderID")
                    }
                    is ApiState.Failure -> {

                    }
                }
            }
        }
    }

    private fun observeExchangeRates() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.exchangeRatesState.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> {

                    }
                    is ApiState.Success -> {

                        updateSubtotal()
                    }
                    is ApiState.Failure -> {
                        Log.e("CartFragment", "Error fetching rates:")
                    }
                }
            }
        }
    }

    private fun handleCheckout() {
        try {
            val updatedItems = draftOrderRequest.draft_order.line_items.toMutableList()
            draftOrderRequest.draft_order = draftOrderRequest.draft_order.copy(line_items = updatedItems)

            favoriteViewModel.updateFavoriteDraftOrder(
                draftOrderID, UpdateDraftOrderRequest(draftOrderRequest.draft_order)
            )
            val paymentDialog = PaymentDialogFragment()
            paymentDialog.show(childFragmentManager, "PaymentDialogFragment")
            Log.d("CartFragment", "Checkout successful")
        } catch (e: Exception) {
            Log.e("CartFragment", "Checkout failed", e)
        }
    }

    private fun updateSubtotal(): Double {
        // Calculate subtotal based on the current items in the cart
        val subtotal = cartAdapter.currentList.sumOf { cartAdapter.calculateItemTotal(it) }
        val currency = sharedPreferences.getString("currency", "EGP")

        // Get the exchange rate based on the current state of exchangeRatesState
        val exchangeRate = when (val ratesState = settingViewModel.exchangeRatesState.value) {
            is ApiState.Success -> ratesState.data  // Get the exchange rate from the Success state
            is ApiState.Loading -> 1.0  // Default to 1.0 while loading
            is ApiState.Failure -> {
                Log.e("CartFragment", "Failed to fetch exchange rates: ${ratesState.message}")
                1.0  // Fallback to 1.0 on failure
            }
        }

        // Calculate converted subtotal based on the exchange rate
        val convertedSubtotal = if (currency == "EGP") {
            subtotal
        } else {
            subtotal / (exchangeRate.takeIf { it != 0.0 } ?: 1.0) // Handle invalid exchange rate gracefully
        }

        // Update the subtotal text view
        val subtotalText = if (currency == "EGP") {
            "Subtotal: EGP %.2f".format(subtotal)
        } else {
            "Subtotal: USD %.2f".format(convertedSubtotal)
        }

        binding.cartSubtotal.text = subtotalText
        Log.d("CartFragment", "Subtotal: $convertedSubtotal")
        return convertedSubtotal
    }

    private fun deleteProduct(lineItem: LineItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            val updatedList = draftOrderRequest.draft_order.line_items.toMutableList().apply {
                remove(lineItem)
            }

            // Only update if the list has changed
            if (updatedList.size != draftOrderRequest.draft_order.line_items.size) {
                draftOrderRequest.draft_order = draftOrderRequest.draft_order.copy(line_items = updatedList)

                try {
                    favoriteViewModel.updateFavoriteDraftOrder(draftOrderID, UpdateDraftOrderRequest(draftOrderRequest.draft_order))
                    cartAdapter.submitList(updatedList)
                    updateSubtotal()
                } catch (e: Exception) {
                    Log.e("CartFragment", "Failed to update draft order: ${e.message}")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCurrencySelected(currency: String) {
        val totalPrice = updateSubtotal()

        when (currency) {
            "cash" -> {

                navigateToCashPayment(totalPrice)
            }
            "visa" -> {

                navigateToVisaPayment(totalPrice)
            }
        }
    }
    private fun navigateToCashPayment(totalPrice: Double) {
        val bundle = Bundle().apply { putDouble("totalPrice", totalPrice) }
        findNavController().navigate(R.id.dummyFragment,bundle)
    }

    private fun navigateToVisaPayment(totalPrice: Double) {

         val bundle = Bundle().apply { putDouble("totalPrice", totalPrice) }
        findNavController().navigate(R.id.action_cartFragment_to_paymentFragment, bundle)
    }
}

