package com.example.mcommerce.ui.CategoryDetails.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcommerce.ui.CategoryDetails.viewModel.ViewModelFactory
import com.example.mcommerce.ui.CategoryDetails.viewModel.viewModelCategoryDetails
import com.example.mcommerce.databinding.FragmentCategoryDetailsBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.responses.ProductResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryDetailsFragment : Fragment() {

    private var _binding: FragmentCategoryDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModelCategoryDetails: viewModelCategoryDetails
    private lateinit var categoryDetailsAdapter: CategoryDetailsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // استلام categoryId باستخدام Safe Args
        val categoryId = CategoryDetailsFragmentArgs.fromBundle(requireArguments()).categoryId
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        categoryDetailsAdapter = CategoryDetailsAdapter({})
        binding.recyclerView.adapter = categoryDetailsAdapter
        // تهيئة ViewModel
        viewModelFactory = ViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        viewModelCategoryDetails = ViewModelProvider(this, viewModelFactory)
            .get(com.example.mcommerce.ui.CategoryDetails.viewModel.viewModelCategoryDetails::class.java)

        // جلب تفاصيل الفئة باستخدام categoryId
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
                            categoryDetailsAdapter.submitList(categoryData)
                            Log.i("CategoryDetailsFragment", "Received data: ${categoryData.size} items")
                        }
                    }
                    is ApiState.Failure -> {
                        Log.e("CategoryDetailsFragment", "Error fetching category details: ${state.message}")
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}