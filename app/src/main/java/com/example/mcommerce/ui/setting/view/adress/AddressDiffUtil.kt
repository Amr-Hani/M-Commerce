package com.example.mcommerce.ui.setting.view.adress

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mcommerce.databinding.AdressItemBinding
import com.example.mcommerce.model.responses.Address


class AddressDiffUtil : DiffUtil.ItemCallback<Address>() {
    override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
        return oldItem == newItem
    }
}

class AddressAdapter(
    private val onDeleteClick: (Long,Boolean) -> Unit
) : ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding =
            AdressItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = getItem(position)

        holder.binding.textViewAddress.text = "${address.address1}, ${address.city}"
        holder.binding.textViewPhone.text = address.phone
        holder.binding.textViewCountry.text = address.country
        holder.binding.imageViewDelete.setOnClickListener {
            onDeleteClick(address.id,address.default)
        }
        if (address.default) {
            // Example: highlight the default address
            holder.binding.textViewAddress.setTextColor(Color.GREEN) // Change color for default address
        }
    }

    class AddressViewHolder(val binding: AdressItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}