package com.example.mcommerce.ui.search.view

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
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory
import com.example.mcommerce.ui.search.viewmodel.SearchFragmentViewModel
import com.example.mcommerce.ui.search.viewmodel.SearchFragmentViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
        lifecycleScope.launch {

        }
    }

    override fun onClick(products: Products) {
        if (products.templateSuffix == "FAVORITE") {
            lifecycleScope.launch {
                if (draftOrderID == 0L) {
                    favoriteViewModel.createFavoriteDraftOrder(draftOrderRequest(products))
                    delay(2000)
                    favoriteViewModel.getAllFavoriteDraftOrders()
                    favoriteViewModel.allDraftOrdersStateFlow.collectLatest {
                        when (it) {
                            is ApiState.Failure -> {}
                            is ApiState.Loading -> {}
                            is ApiState.Success -> {
                                Log.d(
                                    TAG,
                                    "onClick: ana defto hala ${it.data.get(it.data.size - 1).id}"
                                )
                                sharedPreferences.edit().putString(
                                    MyKey.DRAFT_ORDER_ID,
                                    "${it.data.get(it.data.size - 1).id}"
                                ).apply()
                            }
                        }
                    }
                } else {
                    favoriteViewModel.getFavoriteDraftOrder(draftOrderID)
                    favoriteViewModel.draftOrderStateFlow.collectLatest {
                        when (it) {
                            is ApiState.Failure -> {}
                            is ApiState.Loading -> {}
                            is ApiState.Success -> {
                                var oldLineItem: MutableList<LineItem> = mutableListOf()
                                    it.data.draft_order.line_items.forEach {
                                        oldLineItem.add(it)
                                    }
                                    oldLineItem.add(
                                        draftOrderRequest(products).draft_order.line_items.get(0)
                                    )

                                    val draft = draftOrderRequest(products).draft_order

                                    draft.line_items = oldLineItem

                                    favoriteViewModel.updateFavoriteDraftOrder(
                                        draftOrderID,
                                        UpdateDraftOrderRequest(draft)
                                    )
//                                }
//                                else{
//                                    favoriteViewModel.createOrder(draftOrderRequest(products))
//                                }

                            }
                        }
                    }


                }

            }

            //favoriteViewModel.createOrder()
        } else {
            val action =
                SearchFragmentDirections.actionSearchFragmentToProductInfoFragment(products.id)
            Navigation.findNavController(binding.root).navigate(action)
        }

    }


    private fun draftOrderRequest(products: Products): DraftOrderRequest {

        val draftOrderRequest = DraftOrderRequest(
            draft_order = DraftOrder(
                line_items = listOf(
                    LineItem(
                        product_id = products.id,
                        sku = "${products.id}<+>${products.image?.src}",
                        title = products.title, price = products.variants[0].price, quantity = 1
                    )
                ),
                use_customer_default_address = true,
                applied_discount = AppliedDiscount(),
                customer = Customers(8246104457515)
            )

        )
        return draftOrderRequest
    }
}