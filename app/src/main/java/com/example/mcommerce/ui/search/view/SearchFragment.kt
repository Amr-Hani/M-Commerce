package com.example.mcommerce.ui.search.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcommerce.MainActivity
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), OnFavoriteClick<Products>, OnDetailsClick<Products> {
    lateinit var binding: FragmentSearchBinding
    lateinit var searchFragmentViewModelFactory: SearchFragmentViewModelFactory
    lateinit var searchFragmentViewModel: SearchFragmentViewModel
    lateinit var searchFragmentAdapter: SearchFragmentAdapter
    private val TAG = "SearchFragment"
    lateinit var sharedPreferences: SharedPreferences
    var favoriteDraftOrderId: Long = 0
    var customerID: Long = 0

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
        sharedPreferences =
            requireContext().getSharedPreferences(
                MyKey.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
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

        favoriteDraftOrderId = (sharedPreferences.getString(MyKey.MY_FAVORITE_DRAFT_ID, "0")
            ?: "0").toLong()

        customerID = (sharedPreferences.getString(MyKey.MY_CUSTOMER_ID, "0")
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
        searchFragmentAdapter = SearchFragmentAdapter(this, this)
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
                    }
                }
            }
        }
        lifecycleScope.launch {

        }
    }

    override fun onFavoriteClick(products: Products) {
        val guest = sharedPreferences.getString(MyKey.GUEST, "notguest")
        if (guest != "GUEST") {
            lifecycleScope.launch(Dispatchers.IO) {
                favoriteViewModel.getFavoriteDraftOrder(favoriteDraftOrderId)
                favoriteViewModel.draftOrderStateFlow.collectLatest {
                    when (it) {
                        is ApiState.Failure -> {}
                        is ApiState.Loading -> {}
                        is ApiState.Success -> {
                            var oldLineItem: MutableList<LineItem> = mutableListOf()
                            val draftOrderRequest =
                                draftOrderRequest(products).draft_order.line_items.get(0)
                            it.data.draft_order.line_items.forEach {
                                if (draftOrderRequest.title != it.title) {
                                    oldLineItem.add(it)
                                }
                            }
                            oldLineItem.add(
                                draftOrderRequest
                            )

                            val draft = draftOrderRequest(products).draft_order

                            draft.line_items = oldLineItem

                            favoriteViewModel.updateFavoriteDraftOrder(
                                favoriteDraftOrderId,
                                UpdateDraftOrderRequest(draft)
                            )
                        }
                    }
                }
            }
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Regester")
                .setMessage("if you want to add to favorite you must register")
                .setPositiveButton("Yes") { dialog, _ ->
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

    }

    override fun onDetailsClick(details: Products) {
        val action =
            SearchFragmentDirections.actionSearchFragmentToProductInfoFragment(details.id)
        Navigation.findNavController(binding.root).navigate(action)
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
                customer = Customers(customerID)
            )

        )
        return draftOrderRequest
    }


}