package com.example.mcommerce.ui.brandDetails.view

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

import com.example.mcommerce.databinding.FragmentBrandDetailsBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.network.currency.RetrofitInstance
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.ui.brandDetails.viewModel.ViewModelBrand
import com.example.mcommerce.ui.brandDetails.viewModel.ViewModelFactory
import com.example.mcommerceapp.model.network.RemoteDataSourceForCurrency
import com.example.mcommerceapp.model.network.Repo
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class BrandDetails : Fragment() {

    private var _binding: FragmentBrandDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var brandAdapter: BrandDetailsAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModelBrand: ViewModelBrand
    lateinit var sharedPreferences: SharedPreferences
    var currency: String? = null
    var rating: Double = 1.0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandDetailsBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        currency = sharedPreferences.getString("currency", "EGP") ?: "EGP"
        if (currency != "EGP") {
            getRatCurrency()
        }
        return binding.root
    }

    private fun navigateToProductDetails(productId: Long) {
        val action = BrandDetailsDirections.actionBrandDetailsToProductInfoFragment(productId)
        findNavController().navigate(action)
    }

    private fun addToFavorite(products: Products, position: Int) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModelFactory = ViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        viewModelBrand = ViewModelProvider(this, viewModelFactory).get(ViewModelBrand::class.java)


        val brandId = arguments?.let { BrandDetailsArgs.fromBundle(it).brandId }

        // إعداد RecyclerView
        binding.recyclerBrandDetails.layoutManager = GridLayoutManager(requireContext(), 2)
        brandAdapter = BrandDetailsAdapter(
            { productId ->
                navigateToProductDetails(productId)
            },
            { productItem, position -> addToFavorite(productItem, position) }
        )


        binding.recyclerBrandDetails.adapter = brandAdapter

        // Fetch products by brand ID
        brandId?.let {
            viewModelBrand.getproductBrandById(it) // تأكد من أن هذه الدالة تجلب المنتجات بناءً على ID العلامة التجارية
            getBrandProduct()
        }
    }


    private fun getBrandProduct() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModelBrand.brandDetails.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.i("HomeFragment", "Loading state")
                        binding.recyclerBrandDetails.visibility = View.GONE
                    }

                    is ApiState.Success<*> -> {
                        binding.recyclerBrandDetails.visibility = View.VISIBLE

                        // Cast the data to the correct type: List<SmartCollectionsItem>
                        val productList = state.data as? List<ProductResponse>
                        if (productList != null) {
                            if (currency != "EGP") {
                                Thread.sleep(250)
                                productList.get(0).products.forEach {
                                    val totalPrice =
                                        (it.variants.get(0).price.toDouble().toDouble() / rating)
                                    val formattedPrice = String.format("%.2f", totalPrice)
                                    it.variants.get(0).price = "$formattedPrice USD"
                                }
                            } else {
                                productList.get(0).products.forEach {
                                    it.variants.get(0).price += " EGP"
                                }

                            }

                            brandAdapter.submitList(productList.get(0).products)
                            Log.i("HomeFragment1", "Success state: ${productList.size}")
                        } else {
                            Log.e("HomeFragment", "Data is not of expected type")
                        }
                    }

                    is ApiState.Failure -> {
                        Log.i("HomeFragment", "Error: ${state.message}")
                    }
                }
            }
        }
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