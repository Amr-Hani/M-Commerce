package com.example.mcommerce.ui.order.view





import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.mcommerce.databinding.ItemOrderBinding
import com.example.mcommerce.model.responses.orders.OrderElement

class OrderAdapter():ListAdapter<OrderElement,OrderAdapter.ViewModel>(MyDiffUtil()) {



    class ViewModel( private var binding: ItemOrderBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(order: OrderElement) {
            binding.apply {
                // Set brand title
                orderid.text = order.id.toString()
                noOFitemid.text=order.lineItems.get(0).quantity.toString()
                monyPaidid.text=order.totalPrice
                val price = order.totalPrice ?: "0.00"

                val currency = "USD"
                monyPaidid.text = "$price $currency"
                addressid.text=order.shippingAddress.city
                dateid.text=order.createdAt


    }}}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemOrderBinding.inflate(inflater, parent, false)
        return ViewModel(binding)
    }

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }
    class MyDiffUtil():DiffUtil.ItemCallback<OrderElement>() {
        override fun areItemsTheSame(oldItem: OrderElement, newItem: OrderElement): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: OrderElement, newItem: OrderElement): Boolean {
                    return oldItem == newItem
        }
    }
}