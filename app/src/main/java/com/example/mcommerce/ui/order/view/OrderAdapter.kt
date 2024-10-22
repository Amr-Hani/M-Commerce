package com.example.mcommerce.ui.order.view





import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.mcommerce.databinding.ItemOrderBinding
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.responses.ReceivedDraftOrder
import com.example.mcommerce.model.responses.ReceivedOrdersResponse
import com.example.mcommerce.model.responses.orders.OrderElement

class OrderAdapter():ListAdapter<DraftOrderRequest,OrderAdapter.ViewModel>(MyDiffUtil()) {



    class ViewModel( private var binding: ItemOrderBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(order: DraftOrderRequest) {
            binding.apply {
                // Set brand title
                orderid.text = order.draft_order.line_items.get(0).price
                noOFitemid.text=order.draft_order.line_items?.get(0)?.quantity.toString()
                monyPaidid.text=order.draft_order.line_items.get(0).price
                val price = order.draft_order.line_items ?: "0.00"

                val currency = "USD"
                monyPaidid.text = "$price $currency"
                addressid.text=order.draft_order.line_items.get(0).vendor
                dateid.text=order.draft_order.line_items.get(0).title


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
    class MyDiffUtil():DiffUtil.ItemCallback<DraftOrderRequest>() {
        override fun areItemsTheSame(oldItem: DraftOrderRequest, newItem: DraftOrderRequest): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: DraftOrderRequest, newItem: DraftOrderRequest): Boolean {
                    return oldItem == newItem
        }
    }
}