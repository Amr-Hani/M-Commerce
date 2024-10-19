package com.example.mcommerce.ui.cart.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mcommerce.databinding.CartItemBinding
import com.example.mcommerce.model.pojos.prouduct.Product

class CartAdapter(
    private val onItemChanged: () -> Unit,
    private val onItemDeleted: (Product) -> Unit
) : ListAdapter<Product, CartAdapter.CartViewHolder>(ProductCartDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = getItem(position)
        with(holder.binding) {
            Glide.with(productImage.context)
                .load(product.image)
                .into(productImage)

            productName.text = product.name
            productPrice.text = "${product.price * product.quantity} EGP"
            productQuantity.text = product.quantity.toString()

            increaseQuantity.setOnClickListener {
                if (product.quantity < product.stock) {
                    product.quantity++
                    notifyItemChanged(position)
                    onItemChanged()
                }else{
                    Toast.makeText(
                        holder.itemView.context,
                        "Items at stock ${product.stock} you can't add more",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            decreaseQuantity.setOnClickListener {
                if (product.quantity > 1) {
                    product.quantity--
                    notifyItemChanged(position)
                    onItemChanged()
                }
            }
            deleteProduct.setOnClickListener {
                onItemDeleted(product)
            }
        }
    }

    class CartViewHolder(val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root)
}