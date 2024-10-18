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
    private val onItemClick: (ProductResponse) -> Unit // Changed to CustomCollection
) : ListAdapter<ProductResponse, CategoryDetailsAdapter.BrandViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryDetailsBinding.inflate(inflater, parent, false)
        return BrandViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class BrandViewHolder(
        private val binding: ItemCategoryDetailsBinding,
        private val onItemClick: (ProductResponse) -> Unit // Changed to CustomCollection
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: ProductResponse) {
            binding.apply {
                // Set brand title
                pricecategoryDetals.text = categoryItem.products.get(0).variants.get(0).price

                // Load brand image using Glide with options
                Glide.with(imagcategoryDetals.context)
                    .load(categoryItem.products.get(0).images.get(0).src) // Assuming `image.src` contains the image URL
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)) // Caching options
                    .into(imagcategoryDetals)

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
            return oldItem === newItem // Ensure you have an 'id' property
        }

        override fun areContentsTheSame(
            oldItem: ProductResponse,
            newItem: ProductResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}
