package com.example.mcommerce.ui.brandDetails.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.ItemBrandDetailsBinding
import com.example.mcommerce.model.responses.ProductResponse

class BrandDetailsAdapter(
    private val itemClickListener: (ProductResponse) -> Unit, // Listener for item clicks
    private val addClickListener: (ProductResponse, Int) -> Unit // Listener for add button clicks
) : ListAdapter<ProductResponse, BrandDetailsAdapter.ProductsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBrandDetailsBinding.inflate(inflater, parent, false)
        return ProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val productResponse = getItem(position) // Get the ProductResponse at the current position
        holder.bind(productResponse, itemClickListener, addClickListener, position)
    }

    class ProductsViewHolder(private val binding: ItemBrandDetailsBinding) : ViewHolder(binding.root) {

        fun bind(
            productResponse: ProductResponse,
            itemClickListener: (ProductResponse) -> Unit,
            addClickListener: (ProductResponse, Int) -> Unit,
            position: Int
        ) {
            // Loop through each product and display its details
            val products = productResponse.products
            if (products.isNotEmpty()) {
                for (product in products) {
                    // Set product price for the first variant
                    binding.pricetextbrand.text = product.variants.getOrNull(0)?.price ?: "N/A"

                    // Load product image using Glide
                    Glide.with(binding.imagbrandDetailid.context)
                        .load(product.images.getOrNull(0)?.src) // Load the first image if available
                        .into(binding.imagbrandDetailid)

                    // Set click listener for the "add" button
                    binding.favBrandDetailsid.setOnClickListener {
                        addClickListener(productResponse, position)
                    }

                    // Set click listener for the root view (item)
                    binding.root.setOnClickListener {
                        itemClickListener(productResponse)
                    }
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
        override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem.products.getOrNull(0)?.id == newItem.products.getOrNull(0)?.id
        }

        override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }
    }
}

