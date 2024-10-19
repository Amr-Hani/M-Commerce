package com.example.mcommerce.ui.brandDetails.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager

import com.example.mcommerce.databinding.FragmentBrandDetailsBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.ui.brandDetails.viewModel.ViewModelBrand
import com.example.mcommerce.ui.brandDetails.viewModel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class BrandDetails : Fragment() {

    private var _binding:FragmentBrandDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var brandAdapter: BrandDetailsAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModelBrand: ViewModelBrand

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBrandDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun navigateToProductDetails(productId: Long) {
        val action = BrandDetailsDirections.actionBrandDetailsToProductInfoFragment(productId)
        findNavController().navigate(action)
    }
    ///////دي ياعمر عشان تحط الداتا ف fav
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
            {  productId ->
                navigateToProductDetails(productId) },
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
}