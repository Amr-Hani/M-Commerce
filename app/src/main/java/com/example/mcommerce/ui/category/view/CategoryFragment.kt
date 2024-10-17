package com.example.mcommerce.ui.category.view

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
import com.example.mcommerce.databinding.FragmentCategoryBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.ui.category.viewModel.CategoryViewModel
import com.example.mcommerce.ui.category.viewModel.ViewModelCategoryFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {

    private var _binding: FragmentCategoryBinding? = null
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var viewModelFactory: ViewModelCategoryFactory
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // إعداد RecyclerView
        binding.recyclerCategory.layoutManager = GridLayoutManager(requireContext(), 2)
        categoryAdapter = CategoryAdapter { selectedCategory ->
            // التنقل إلى CategoryDetailsFragment عند الضغط على أي عنصر
            navigateToCategoryDetailsFragment(selectedCategory.id.toString())
        }
        binding.recyclerCategory.adapter = categoryAdapter

        // تهيئة ViewModel باستخدام factory
        viewModelFactory = ViewModelCategoryFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        categoryViewModel = ViewModelProvider(this, viewModelFactory).get(CategoryViewModel::class.java)

        // جلب البيانات من ViewModel
        getBrandProduct()
        categoryViewModel.getCategory("id")
    }

    private fun navigateToCategoryDetailsFragment(categoryId: String) {
        val action = CategoryFragmentDirections.actionNavigationCategoryToCategoryDetailsFragment(categoryId)
        findNavController().navigate(action)
    }

    private fun getBrandProduct() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            categoryViewModel.category.collect { state ->
                when (state) {
                    is ApiState.Loading -> {
                        Log.i("CategoryFragment", "Loading state")
                    }
                    is ApiState.Success<*> -> {
                        val productList = state.data as? List<CustomCollection>
                        if (productList != null) {
                            categoryAdapter.submitList(productList)
                            Log.i("CategoryFragment", "Success state: ${productList.size} items")
                        } else {
                            Log.e("CategoryFragment", "Data is not of expected type")
                        }
                    }
                    is ApiState.Failure -> {
                        Log.i("CategoryFragment", "Error: ${state.message}")
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
