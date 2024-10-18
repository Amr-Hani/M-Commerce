package com.example.mcommerce.ui.category.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.ItemCategoryBinding
import com.example.mcommerce.model.pojos.CategoryPOJO
import com.example.mcommerce.model.pojos.CustomCollection


class CategoryAdapter(
    private val onItemClick: (CustomCollection) -> Unit
) : ListAdapter<CustomCollection, CategoryAdapter.BrandViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BrandViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryBinding.inflate(inflater, parent, false)
        return BrandViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BrandViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.bind(currentItem)
    }

    class BrandViewHolder(
        private var binding: ItemCategoryBinding,
        private val onItemClick: (CustomCollection) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: CustomCollection) {
            binding.apply {
                // Set brand title
               pricecategory.text = categoryItem.title


                // Load brand image using Glide
                Glide.with(imagcategory.context)
                    .load(categoryItem.image?.src) // Assuming `image.src` contains the image URL
                    .into(imagcategory)

                // Handle item click
                root.setOnClickListener {
                    onItemClick(categoryItem)
                }
            }
        }
    }

    class MyDiffUtil : DiffUtil.ItemCallback<CustomCollection>() {
        override fun areItemsTheSame(
            oldItem: CustomCollection,
            newItem: CustomCollection
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: CustomCollection,
            newItem: CustomCollection
        ): Boolean {
            return oldItem == newItem
        }
    }
}