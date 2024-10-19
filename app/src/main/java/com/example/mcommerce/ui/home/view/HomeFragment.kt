package com.example.mcommerce.ui.home.view

import BrandAdapter
import SmartCollectionsItem
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.example.mcommerce.databinding.FragmentHomeBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository

import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.ui.home.viewModel.HomeViewFactory
import com.example.mcommerce.ui.home.viewModel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var brandAdapter: BrandAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewFactory: HomeViewFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView
        binding.recycleridBrand.layoutManager = GridLayoutManager(requireContext(), 2)
        brandAdapter = BrandAdapter({})
        binding.recycleridBrand.adapter = brandAdapter

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

        // Fetch brand products
        homeViewModel.getbrands()

        // Collect and observe the API state from the ViewModel
        getBrandProduct()
        //نا هنا عملت تعديل  brandid = 503034675499
        navigationtoBrandDetails(brandid = 503034675499)
    }

    private fun getBrandProduct() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.productsbrandId.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.i("HomeFragment", "Loading state")
                        binding.recycleridBrand.visibility = View.GONE
                    }
                    is ApiState.Success<*> -> {
                        binding.recycleridBrand.visibility = View.VISIBLE

                        // Cast the data to the correct type: List<SmartCollectionsItem>
                        val productList = state.data as? List<SmartCollectionsItem>
                        if (productList != null) {
                            brandAdapter.submitList(productList)
                            Log.i("HomeFragment", "Success state: $productList")
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



    fun navigationtoBrandDetails(brandid: Long) {
        val onItemClicked: (SmartCollectionsItem) -> Unit = { smartCollectionsItem ->
            // تمرير smartCollectionsItem.id في التنقل للوجهة BrandDetailsFragment
            val action = smartCollectionsItem.id?.let {
                HomeFragmentDirections.actionNavigationHomeToBrandDetails(
                    it
                )
            }
            if (action != null) {
                findNavController().navigate(action)
            }
        }


        brandAdapter = BrandAdapter(onItemClicked)
        binding.recycleridBrand.adapter = brandAdapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
