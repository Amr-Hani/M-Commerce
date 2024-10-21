package com.example.mcommerce.ui.order.view





import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.mcommerce.databinding.ItemOrderBinding
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.orders.OrderElement

class OrderAdapter():ListAdapter<ReceivedDraftOrder,OrderAdapter.ViewModel>(MyDiffUtil()) {



    class ViewModel( private var binding: ItemOrderBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(order: ReceivedDraftOrder) {
            binding.apply {
                // Set brand title
                orderid.text = order.id.toString()
                noOFitemid.text=order.line_items?.get(0)?.quantity.toString()
                monyPaidid.text=order.total_price
                val price = order.total_price ?: "0.00"

                val currency = "USD"
                monyPaidid.text = "$price $currency"
                addressid.text=order.shipping_address?.city
                dateid.text=order.created_at


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
    class MyDiffUtil():DiffUtil.ItemCallback<ReceivedDraftOrder>() {
        override fun areItemsTheSame(oldItem: ReceivedDraftOrder, newItem: ReceivedDraftOrder): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ReceivedDraftOrder, newItem: ReceivedDraftOrder): Boolean {
                    return oldItem == newItem
        }
    }
}