package com.example.mcommerce.ui.search.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentSearchBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.AppliedDiscount
import com.example.mcommerce.model.pojos.Customers
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.example.mcommerce.ui.search.viewmodel.SearchFragmentViewModel
import com.example.mcommerce.ui.search.viewmodel.SearchFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), OnClick<Products> {
    lateinit var binding: FragmentSearchBinding
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    lateinit var searchFragmentViewModel: SearchFragmentViewModel
    lateinit var searchFragmentAdapter: SearchFragmentAdapter
    private val TAG = "SearchFragment"
    lateinit var sharedPreferences: SharedPreferences
    var draftOrderID: Long = 0

    //////////////////////////////////
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    /////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ///////////////////////////////
        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)


        ///
        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        draftOrderID = (sharedPreferences.getString(MyKey.DRAFT_ORDER_ID, "0")
            ?: "0").toLong()


        ///////////////////////////////


        searchFragmentViewModelFactory = SearchFragmentViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        searchFragmentViewModel = ViewModelProvider(
            this,
            searchFragmentViewModelFactory
        ).get(SearchFragmentViewModel::class.java)
        searchFragmentAdapter = SearchFragmentAdapter(this)
        binding.recyclerView2.apply {
            adapter = searchFragmentAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
        searchFragmentViewModel.getProduct()
        lifecycleScope.launch {
            searchFragmentViewModel.SearchProductStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> Log.e(TAG, "onViewCreated: Failure")
                    is ApiState.Loading -> Log.d(TAG, "onViewCreated: Loading")
                    is ApiState.Success -> {
                        binding.searchView.setOnQueryTextListener(object :
                            SearchView.OnQueryTextListener {
                            override fun onQueryTextSubmit(query: String?): Boolean {
                                return false
                            }

                            override fun onQueryTextChange(newText: String?): Boolean {
                                lifecycleScope.launch {
                                    if (newText.isNullOrEmpty()) {
                                        searchFragmentAdapter.submitList(emptyList())
                                    } else {
                                        val filteredList = withContext(Dispatchers.Default) {
                                            it.data.filter { item ->
                                                item.title.contains(newText, ignoreCase = true)
                                            }
                                        }
                                        searchFragmentAdapter.submitList(filteredList)
                                    }
                                }
                                return true
                            }
                        })

                        // searchFragmentAdapter.submitList(it.data)
                    }
                }
            }
        }
    }

    override fun onClick(click: Products) {
        if (click.templateSuffix == "FAVORITE") {
            favoriteViewModel.getAllDraftOrders()
            lifecycleScope.launch {
                if (draftOrderID == 0L) {
                    favoriteViewModel.createOrder(createDraftOrderRequest(click))
                }else{

                }

            }
            //favoriteViewModel.createOrder()
        } else {
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductInfoFragment(click.id)
            Navigation.findNavController(binding.root).navigate(action)
        }


    }
    fun createDraftOrderRequest(products: Products): DraftOrderRequest {
        return DraftOrderRequest(
            draft_order = DraftOrder(
                line_items = listOf(
                    LineItem(
                        product_id = products.id,
                        title = "Sample Product",
                        price = "50.00"
                    ),
                    LineItem(
                        product_id = 54321L,
                        title = "Another Product",
                        price = "30.00"
                    )
                ),
                applied_discount = AppliedDiscount(
                    description = "10% off",
                    value_type = "percentage",
                    value = "10",
                    amount = "5.00",
                    title = "Discount"
                ),
                customer = Customers(
                    id = 8246104654123
                ),
                use_customer_default_address = true
            )
        )
    }
}