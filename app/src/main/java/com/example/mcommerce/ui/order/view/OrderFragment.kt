package com.example.mcommerce.ui.order.view


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentOrderBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.model.responses.orders.Order
import com.example.mcommerce.model.responses.orders.OrderElement
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.order.viewModel.OrderViewModel
import com.example.mcommerce.ui.order.viewModel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var viewModel: OrderViewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private var draftOrderID: Long = 0
    private var customerID: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter()
        binding.recyclerview.adapter = orderAdapter
        viewModelFactory = ViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(OrderViewModel::class.java)
        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        draftOrderID = (sharedPreferences.getString(MyKey.MY_CARD_DRAFT_ID, "1")
            ?: "1").toLong()
        customerID = (sharedPreferences.getString(MyKey.MY_CUSTOMER_ID, "1")
            ?: "1").toLong()


        viewModel.getCustomerOrders(draftOrderID)
//        getCustomerOrder()
        getOrderById()
    }

//    private fun getCustomerOrder() {
//        lifecycleScope.launch(Dispatchers.Main) {
//            viewModel.order.collect { state ->
//                when (state) {
//                    is ApiState.Failure -> {
//                        Log.d("Apistate", "getCustomerOrder: ${state.message}")
//                    }
//
//                    is ApiState.Success -> {
//
//                    }
//
//                    is ApiState.Loading -> {
//                        Log.i("Apistate", "Loading customer orders...")
//                    }
//                }
//            }
//        }
//    }


    fun getOrderById() {
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.getOrdersByCustomerId(customerID.toString())
            viewModel.getOrdersByCustomerIdStateFlow.collectLatest {
                when(it){
                    is ApiState.Failure -> {
                        Log.d("TAG", "getOrderById faillllllllll: ${it.message}")}
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        Log.d("TAG", "getOrderById: ${it.data}")
                        orderAdapter.submitList(it.data.orders)

                    }
                }
            }
        }
    }

}