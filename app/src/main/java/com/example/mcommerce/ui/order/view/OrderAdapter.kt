//package com.example.mcommerce.ui.order.view
//
//
//import DraftOrderElement
//
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//
//import com.example.mcommerce.databinding.ItemOrderBinding
//
//class OrderAdapter():ListAdapter<DraftOrderElement,OrderAdapter.ViewModel>(MyDiffUtil()) {
//
//
//
//    class ViewModel( private var binding: ItemOrderBinding):RecyclerView.ViewHolder(binding.root){
//
//        fun bind(order: DraftOrderElement) {
//            binding.apply {
//                // Set brand title
//                orderid.text = order.orderID.toString()
//                noOFitemid.text=order.id.toString()
//                monyPaidid.text=order.totalPrice.toString()
//                addressid.text=order.billingAddress.address1
//                dateid.text=order.createdAt
//
//
//    }}}
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewModel {
//        val inflater = LayoutInflater.from(parent.context)
//        val binding = ItemOrderBinding.inflate(inflater, parent, false)
//        return ViewModel(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewModel, position: Int) {
//        val currentItem = getItem(position)
//        holder.bind(currentItem)
//    }
//    class MyDiffUtil():DiffUtil.ItemCallback<DraftOrderElement>() {
//        override fun areItemsTheSame(oldItem: DraftOrderElement, newItem: DraftOrderElement): Boolean {
//            return oldItem === newItem
//        }
//
//        override fun areContentsTheSame(oldItem: DraftOrderElement, newItem: DraftOrderElement): Boolean {
//                    return oldItem == newItem
//        }
//    }
//}