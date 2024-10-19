package com.example.mcommerce.ui.CategoryDetails.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mcommerce.databinding.ItemCategoryDetailsBinding
import com.example.mcommerce.model.pojos.Products

import com.example.mcommerce.model.responses.ProductResponse

class CategoryDetailsAdapter(
    private val onItemClick: (Long) -> Unit // Handle click with product ID
) : ListAdapter<Products, CategoryDetailsAdapter.ProductViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryDetailsBinding.inflate(inflater, parent, false)
        return ProductViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ProductViewHolder(
        private val binding: ItemCategoryDetailsBinding,
        private val onItemClick: (Long) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Products) {
            binding.apply {
                // Bind the product price
                pricecategoryDetals.text = product.variants.getOrNull(0)?.price ?: "No Price"

                // Load product image, if available
                Glide.with(imagcategoryDetals.context)
                    .load(product.images.get(0).src) // Load the first image if available
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imagcategoryDetals)

                // Set the click listener to handle item clicks
                root.setOnClickListener {
                    onItemClick(product.id) // Pass the product's ID on click
                }


            }
        }
    }

    // Custom DiffUtil class to improve performance
    class MyDiffUtil : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            // Use product ID for comparison
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            // Use product's full equality check (you may need to override equals in Product class)
            return oldItem == newItem
        }
    }
}