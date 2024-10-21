package com.example.mcommerce.ui.favorite.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.ShowFavoritePorductsBinding
import com.example.mcommerce.model.pojos.LineItem

class FavoriteFragmentAdapter (val onFavoriteClick: OnFavoriteClick,val onProductDetails: OnProductDetails):
    ListAdapter<LineItem, FavoriteFragmentAdapter.FavoriteFragmentViewHolder>(
        FavoriteFragmentDiffUtil()
    ) {
    lateinit var binding: ShowFavoritePorductsBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteFragmentViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ShowFavoritePorductsBinding.inflate(layoutInflater, parent, false)
        return FavoriteFragmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteFragmentViewHolder, position: Int) {
        val currentDraftOrderRequest = getItem(position)
        holder.binding.tvFavoriteName.text = productName(currentDraftOrderRequest.title)
        
        val split = currentDraftOrderRequest.sku?.split("<+>")
        Log.d("TAG", "onBindViewHolder: $split")
    
            Glide.with(holder.itemView.context).load(split?.get(1))
            .into(holder.binding.ivCurrentIconFavorite)

        holder.binding.root.setOnClickListener {
            onProductDetails.onProductDetails(split?.get(0)!!.toLong())
        }
        holder.binding.ivIconFavorite.setColorFilter(Color.RED)

        holder.binding.ivIconFavorite.setOnClickListener {
            onFavoriteClick.onFavoriteClick(currentDraftOrderRequest)
        }
    }

    class FavoriteFragmentViewHolder(val binding: ShowFavoritePorductsBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun productName(productName: String): String {
        val split = productName.split("|")
        return if (split.size > 1) split[1].trim() else productName
    }


}