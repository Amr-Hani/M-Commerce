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
import com.example.mcommerce.ui.category.viewModel.CategoryViewModel
import com.example.mcommerce.ui.category.viewModel.ViewModelCategoryFactory
import com.example.mcommerce.ui.placedOrder.viewModel.PlaceViewModelFactory
import com.example.mcommerce.ui.placedOrder.viewModel.ViewModelPlaceOrder
import kotlinx.coroutines.launch


class PlacedOrderFragment : Fragment() {

    private lateinit var binding: FragmentPlacedOrderBinding
    private lateinit var viewModel: ViewModelPlaceOrder
    private lateinit var viewModelFactory: PlaceViewModelFactory


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlacedOrderBinding.inflate(inflater, container, false)






        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.confirmOrder()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModelFactory = PlaceViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )

        // تهيئة الـ ViewModel باستخدام viewModelFactory
        viewModel = ViewModelProvider(this, viewModelFactory).get(ViewModelPlaceOrder::class.java)


        observeOnStateFlow()
//        observeOnLiveData()

    }


  //  دي حاجتي انا مهيشها لحد لم اتخلص شغل هركبها ه

//    private fun observeOnLiveData() {
//        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            if (isLoading) {
//                Alert.showProgressDialog(requireContext())
//            } else {
//                Alert.hideProgressDialog()
//            }
//        }
//    }

    private fun observeOnStateFlow() {
        lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    is ApiState.Failure<*> -> {
                        Log.d("ApiState", "observeOnStateFlow:${it.message} ")
                    }

                    is ApiState.Success<*> -> {
//                        viewModel.deleteCartItemsById()
                       //// هنا هحط id الكاv]ب ال همسح بيه
                        findNavController().popBackStack(R.id.navigation_home, false)

                    }
                    is ApiState.Loading -> {

                    }

                }
            }
//            }
        }
    }




}