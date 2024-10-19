package com.example.mcommerce.ui.search.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.FragmentSearchBinding
import com.example.mcommerce.databinding.SearchViewBinding
import com.example.mcommerce.model.pojos.Products

class SearchFragmentAdapter(private val onClick: OnClick<Products>) :
    ListAdapter<Products, SearchFragmentAdapter.SearchFragmentAdapterViewHolder>(
        SearchFragmentDiffUtil()
    ) {
    lateinit var binding: SearchViewBinding

    class SearchFragmentAdapterViewHolder(val binding: SearchViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchFragmentAdapterViewHolder {
        val layoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = SearchViewBinding.inflate(layoutInflater, parent, false)
        return SearchFragmentAdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchFragmentAdapterViewHolder, position: Int) {
        val currentProduct = getItem(position)
        holder.binding.tvSearchName.text = productName(currentProduct.title)
        if (!currentProduct.image?.src.isNullOrBlank()) {
            Glide.with(binding.root.context).load(currentProduct.image?.src)
                .into(holder.binding.ivIconSearch)
        }

        binding.root.setOnClickListener {
            onClick.onClick(currentProduct)
        }
        holder.binding.ivIconFavorite.setOnClickListener {
            currentProduct.templateSuffix = "FAVORITE"

            //holder.binding.ivIconFavorite.setColorFilter(Color.RED)

            onClick.onClick(currentProduct)

        }
    }

    private fun productName(productName: String): String {
        val split = productName.split("|")
        return if (split.size > 1) split[1].trim() else productName

    }

}