package com.example.mcommerce.ui.brandDetails.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.mcommerce.databinding.ItemBrandDetailsBinding
import com.example.mcommerce.model.pojos.Products

class BrandDetailsAdapter(
    private val itemClickListener: (Long) -> Unit, // Listener for item clicks, now passing product ID
    private val addClickListener: (Products, Int) -> Unit // Listener for add button clicks
) : ListAdapter<Products, BrandDetailsAdapter.ProductsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBrandDetailsBinding.inflate(inflater, parent, false)
        return ProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = getItem(position) // Get the Product at the current position
        holder.bind(product, itemClickListener, addClickListener, position)
    }

    class ProductsViewHolder(private val binding: ItemBrandDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            product: Products,
            itemClickListener: (Long) -> Unit, // Pass product ID to the listener
            addClickListener: (Products, Int) -> Unit,
            position: Int
        ) {
            binding.apply {
                // Bind the product price
                val price = product.variants.getOrNull(0)?.price ?: "0.00"
                titleeid.text=product.title

                val currency = "USD"
                pricetextbrand.text = "$price"

                // Load product image using Glide, if available
                Glide.with(imagbrandDetailid.context)
                    .load(product.images.getOrNull(0)?.src) // Load the first image if available
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imagbrandDetailid)

                // Set the click listener to handle item clicks and pass the product ID
                root.setOnClickListener {
                    itemClickListener(product.id) // Pass the product's ID on click
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Products>() {
        override fun areItemsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem.id == newItem.id // Compare by product ID
        }

        override fun areContentsTheSame(oldItem: Products, newItem: Products): Boolean {
            return oldItem == newItem
        }
    }
}
