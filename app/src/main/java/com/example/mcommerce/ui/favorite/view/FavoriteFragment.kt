package com.example.mcommerce.ui.favorite.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mcommerce.databinding.FragmentFavoriteBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var favoriteFragmentAdapter: FavoriteFragmentAdapter
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    var draftOrderID: Long = 0
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

        draftOrderID = (sharedPreferences.getString(MyKey.DRAFT_ORDER_ID, "0")
            ?: "0").toLong()

        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        favoriteFragmentAdapter = FavoriteFragmentAdapter()
        binding.rvFavorite.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = favoriteFragmentAdapter
        }
        lifecycleScope.launch {
            if (draftOrderID != 0L) {
                favoriteViewModel.getFavoriteDraftOrder(draftOrderID)
                favoriteViewModel.draftOrderStateFlow.collectLatest {
                    when (it) {
                        is ApiState.Failure -> {}
                        is ApiState.Loading -> {}
                        is ApiState.Success -> {
                            favoriteFragmentAdapter.submitList(it.data.draft_order.line_items)
                        }
                    }
                }
            }
        }
    }
}