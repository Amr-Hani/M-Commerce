//package com.example.mcommerce.ui.order.view
//
//
//import DraftOrder
//import androidx.fragment.app.viewModels
//import android.os.Bundle
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.lifecycleScope
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.mcommerce.R
//import com.example.mcommerce.databinding.FragmentOrderBinding
//import com.example.mcommerce.model.network.ApiState
//import com.example.mcommerce.model.network.ProductInfoRetrofit
//import com.example.mcommerce.model.network.RemoteDataSource
//import com.example.mcommerce.model.network.Repository
//import com.example.mcommerce.ui.order.viewModel.OrderViewModel
//import com.example.mcommerce.ui.order.viewModel.ViewModelFactory
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.launch
//
//class OrderFragment : Fragment() {
//private lateinit var orderAdapter: OrderAdapter
//private lateinit var viewModel: OrderViewModel
//private lateinit var viewModelFactory: ViewModelFactory
//    private var _binding: FragmentOrderBinding? = null
//    private val binding get() = _binding!!
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // تهيئة binding هنا
//        _binding = FragmentOrderBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding.recyclerview.layoutManager=LinearLayoutManager(requireContext())
//        orderAdapter=OrderAdapter()
//        binding.recyclerview.adapter=orderAdapter
//        viewModelFactory=ViewModelFactory(Repository.getInstance(
//            RemoteDataSource(ProductInfoRetrofit.productService)
//        ))
//        viewModel=ViewModelProvider(this,viewModelFactory)
//            .get(OrderViewModel::class.java)
//
//       //ا
//        viewModel.getCustomerOrders("994118539" )
//        getCustomerOrder()
//    }
//
//    private fun getCustomerOrder() {
//       lifecycleScope.launch(Dispatchers.Main){
//           viewModel.order.collect{ state->
//               when(state){
//                   is ApiState.Failure->{
//                       Log.d("Apistate", "getCustomerOrder:${state.message} ")
//                   }
//                   is ApiState.Success->{
//                       val customer = state.data as? List<DraftOrder>
//                       if (customer != null) {
//                           orderAdapter.submitList(customer.get(0).draftOrders)
//                           Log.i("Apistate", "Success state: $customer")
//                       } else {
//                           Log.e("Apistate", "Data is not of expected type")
//                       }
//                   }
//                   is ApiState.Loading->{
//                       Log.i("Apistate", "getCustomerOrder: ")
//                   }
//               }
//           }
//       }
//    }
//
//}