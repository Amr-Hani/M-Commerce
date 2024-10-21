package com.example.mcommerce.ui.cart.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.CartItemBinding
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.prouduct.Product

class CartAdapter(
    private val onItemChanged: () -> Unit,
    private val onItemDeleted: (LineItem) -> Unit
) : ListAdapter<LineItem, CartAdapter.CartViewHolder>(ProductCartDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        val split = product.sku?.split("<+>").orEmpty()
        val imageUrl = split.getOrNull(1) ?: "default_image_url_or_placeholder"

        with(holder.binding) {
            Glide.with(productImage.context)
                .load(imageUrl)
                .into(productImage)

            productName.text = product.name
            productPrice.text = "${"${calculateItemTotal(product)} EGP"} "
            productQuantity.text = product.quantity.toString()

            increaseQuantity.setOnClickListener {
                if (product.quantity!! < 5) {
                    product.quantity = product.quantity!! + 1
                    notifyItemChanged(position)
                    onItemChanged()
                }else{
                    Toast.makeText(
                        holder.itemView.context,
                        "Items at stock ${product.quantity} you can't add more",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            decreaseQuantity.setOnClickListener {
                if (product.quantity!! > 1) {
                    product.quantity = product.quantity!! - 1
                    notifyItemChanged(position)
                    onItemChanged()
                }
            }
            deleteProduct.setOnClickListener {
                onItemDeleted(product)
            }
        }
    }

    fun calculateItemTotal(product: LineItem): Double {
        return product.price.toDouble() * (product.quantity?.toInt() ?: 1)
    }
    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)
}