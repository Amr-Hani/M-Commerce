package com.example.mcommerce.ui.CategoryDetails.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mcommerce.databinding.ItemCategoryDetailsBinding
import com.example.mcommerce.model.pojos.CustomCollection
import com.example.mcommerce.model.responses.ProductResponse

class CategoryDetailsAdapter(
    private val onItemClick: (ProductResponse) -> Unit
) : ListAdapter<ProductResponse, CategoryDetailsAdapter.BrandViewHolder>(BrandViewHolder.MyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryDetailsBinding.inflate(inflater, parent, false)
        return BrandViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position) // Get the correct item at the current position
        holder.bind(currentItem) // Bind the item at this position
    }

    class BrandViewHolder(
        private val binding: ItemCategoryDetailsBinding,
        private val onItemClick: (ProductResponse) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: ProductResponse) {
            binding.apply {

                for (product in categoryItem.products) {


                    pricecategoryDetals.text = product.variants.getOrNull(0)?.price ?: "No Price"


                    Glide.with(imagcategoryDetals.context)
                        .load(product.images.getOrNull(0)?.src)
                        .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                        .into(imagcategoryDetals)

                    // Handle item click for the whole category
                    root.setOnClickListener {
                        onItemClick(categoryItem)
                    }

                    // Handle item click
                    root.setOnClickListener {
                        onItemClick(categoryItem)
                        //هنا انترفيس الى هتديلو ي عم منر ال ProductId
                        //categoryItem.products.get(0).id

                    }

                }
            }
        }


        class MyDiffUtil : DiffUtil.ItemCallback<ProductResponse>() {
            override fun areItemsTheSame(
                oldItem: ProductResponse,
                newItem: ProductResponse
            ): Boolean {
                // Compare unique IDs or another unique identifier
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: ProductResponse,
                newItem: ProductResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }}

