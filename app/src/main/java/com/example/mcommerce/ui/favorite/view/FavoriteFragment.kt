package com.example.mcommerce.ui.favorite.view

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcommerce.databinding.FragmentFavoriteBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment(), OnFavoriteClick,OnProductDetails {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var favoriteFragmentAdapter: FavoriteFragmentAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    var customerId: Long = 0
    var favoriteDraftOrderId: Long = 0
    lateinit var draftOrderRequest: DraftOrderRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        favoriteDraftOrderId = (sharedPreferences.getString(MyKey.MY_FAVORITE_DRAFT_ID, "0")
            ?: "0").toLong()

        customerId = (sharedPreferences.getString(MyKey.MY_CUSTOMER_ID, "0")
            ?: "0").toLong()

        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        favoriteFragmentAdapter = FavoriteFragmentAdapter(this,this)
        binding.rvFavorite.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteFragmentAdapter
        }
        lifecycleScope.launch {
            favoriteViewModel.getFavoriteDraftOrder(favoriteDraftOrderId)
            favoriteViewModel.draftOrderStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {
                        Log.e("TAG", "onViewCreated: faillllllllllllll")
                    }

                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        val mutableList: MutableList<LineItem> = mutableListOf()
                        draftOrderRequest = it.data
                        it.data.draft_order.line_items.forEach {
                            if (it.sku != "null") {
                                mutableList.add(it)
                            }
                        }
                        favoriteFragmentAdapter.submitList(mutableList)
                    }
                }
            }
        }
    }
    override fun onProductDetails(productId: Long) {
        val action = FavoriteFragmentDirections.actionFavoriteFragmentToProductInfoFragment(productId)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onFavoriteClick(lineItem: LineItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Deletion")
            .setMessage("Do You Want Remove This Item From Favorite")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    if (draftOrderRequest.draft_order.line_items.size > 1) {
                        val mutableLineItems: MutableList<LineItem> = mutableListOf()
                        draftOrderRequest.draft_order.line_items.forEach {
                            if (it != lineItem) {
                                mutableLineItems.add(it)
                            }
                        }
                        draftOrderRequest.draft_order.line_items = mutableLineItems
                        favoriteViewModel.updateFavoriteDraftOrder(
                            favoriteDraftOrderId,
                            UpdateDraftOrderRequest(draftOrderRequest.draft_order)
                        )
                        delay(500)
                        favoriteViewModel.getFavoriteDraftOrder(favoriteDraftOrderId)
                    } else {
                        draftOrderRequest.draft_order.line_items.get(0).sku = "null"
                        favoriteViewModel.updateFavoriteDraftOrder(
                            favoriteDraftOrderId,
                            UpdateDraftOrderRequest(draftOrderRequest.draft_order)
                        )
                        delay(500)
                        favoriteViewModel.getFavoriteDraftOrder(favoriteDraftOrderId)
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }


}