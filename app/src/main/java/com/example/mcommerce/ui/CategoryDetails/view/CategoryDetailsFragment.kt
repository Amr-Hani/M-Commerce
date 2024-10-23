package com.example.mcommerce.ui.CategoryDetails.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcommerce.ui.CategoryDetails.viewModel.ViewModelFactory
import com.example.mcommerce.ui.CategoryDetails.viewModel.viewModelCategoryDetails
import com.example.mcommerce.databinding.FragmentCategoryDetailsBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.network.currency.RetrofitInstance
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerceapp.model.network.RemoteDataSourceForCurrency
import com.example.mcommerceapp.model.network.Repo
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryDetailsFragment : Fragment() {
    private var _binding: FragmentCategoryDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModelCategoryDetails: viewModelCategoryDetails
    private lateinit var categoryDetailsAdapter: CategoryDetailsAdapter
    lateinit var sharedPreferences: SharedPreferences
    var currency: String? = null
    var rating = 1.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailsBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        currency = sharedPreferences.getString("currency", "EGP") ?: "EGP"
        if (currency != "EGP") {
            getRatCurrency()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryId = CategoryDetailsFragmentArgs.fromBundle(requireArguments()).categoryId
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        categoryDetailsAdapter = CategoryDetailsAdapter { productId ->
            ///ana hena bankl l 3mr productDetails
            val action =
                CategoryDetailsFragmentDirections.actionCategoryDetailsFragmentToProductInfoFragment(
                    productId
                )
            findNavController().navigate(action)
        }

        binding.recyclerView.adapter = categoryDetailsAdapter

        viewModelFactory = ViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        viewModelCategoryDetails = ViewModelProvider(this, viewModelFactory)
            .get(com.example.mcommerce.ui.CategoryDetails.viewModel.viewModelCategoryDetails::class.java)

        getCategoryDetails(categoryId.toLong())
    }

    private fun getCategoryDetails(id: Long) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModelCategoryDetails.getProductCategoryById(id)
            viewModelCategoryDetails.categoryDetails.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.i("CategoryDetailsFragment", "Loading data for $id")
                    }

                    is ApiState.Success<*> -> {
                        val categoryData = state.data as? List<ProductResponse>
                        if (categoryData != null) {
                            if (currency != "EGP") {
                                Thread.sleep(250)
                                categoryData.get(0).products.forEach {
                                    val totalPrice =
                                        (it.variants.get(0).price.toDouble().toDouble() / rating)
                                    val formattedPrice = String.format("%.2f", totalPrice)
                                    it.variants.get(0).price = "$formattedPrice USD"
                                }
                            } else {
                                categoryData.get(0).products.forEach {
                                    it.variants.get(0).price += " EGP"
                                }
                            }
                            categoryDetailsAdapter.submitList(categoryData.get(0).products)
                            Log.i(
                                "CategoryDetailsFragment",
                                "Received data: ${categoryData.size} items"
                            )
                        }
                    }

                    is ApiState.Failure -> {
                        Log.e(
                            "CategoryDetailsFragment",
                            "Error fetching category details: ${state.message}"
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService)),
            Repo.getInstance(RemoteDataSourceForCurrency(RetrofitInstance.exchangeRateApi))
        )
    }

    fun getRatCurrency() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.fetchLatestRates()
            settingViewModel.exchangeRatesState.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> {

                    }

                    is ApiState.Success -> {

                        rating = state.data
                    }

                    is ApiState.Failure -> {
                        Log.e("CartFragment", "Error fetching rates:")
                    }
                }
            }
        }
    }
}
