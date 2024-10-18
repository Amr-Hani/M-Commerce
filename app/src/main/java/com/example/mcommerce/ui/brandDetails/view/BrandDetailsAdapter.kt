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
    private val itemClickListener: (ProductResponse) -> Unit, // دالة لاستماع النقرات على العناصر
    private val addClickListener: (ProductResponse, Int) -> Unit // دالة لاستماع النقرات على زر الإضافة
) : ListAdapter<ProductResponse, BrandDetailsAdapter.ProductsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBrandDetailsBinding.inflate(inflater, parent, false)
        return ProductsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        holder.bind(getItem(position), itemClickListener, addClickListener, position)
    }

    class ProductsViewHolder(val binding: ItemBrandDetailsBinding) : ViewHolder(binding.root) {

        fun bind(
            productItem: ProductResponse,
            itemClickListener: (ProductResponse) -> Unit,
            addClickListener: (ProductResponse, Int) -> Unit,
            position: Int
        ) {
            // تعيين البيانات في الواجهة
            binding.pricetextbrand.text = productItem.products.get(0).variants.get(0).price
            Glide.with(binding.imagbrandDetailid.context)
                .load(productItem.products.get(0).image?.src)
                .into(binding.imagbrandDetailid)

            // تعيين مستمع النقر على زر الإضافة
            binding.favBrandDetailsid.setOnClickListener {
                addClickListener(productItem, position)
            }

            // تعيين مستمع النقر على العنصر (إذا كنت ترغب في إضافة ذلك)
            binding.root.setOnClickListener {
                itemClickListener(productItem)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ProductResponse>() {
        override fun areItemsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem.products.get(0).id == newItem.products.get(0).id
        }

        override fun areContentsTheSame(oldItem: ProductResponse, newItem: ProductResponse): Boolean {
            return oldItem == newItem
        }
    }
}

