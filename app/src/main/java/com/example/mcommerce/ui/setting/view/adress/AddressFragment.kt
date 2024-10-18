package com.example.mcommerce.ui.setting.view.adress

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentAddressBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.responses.Address
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.launch

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding
   // private lateinit var sharedPreferences: SharedPreferences
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var settingViewModel: SettingViewModel
    val customerId =8246104654123

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       // sharedPreferences = requireActivity().getSharedPreferences("user_address", Context.MODE_PRIVATE)

        setupAdapters()
        setupViewModel()
        setupObservers()

        binding.btnADD.setOnClickListener {
            findNavController().navigate(R.id.action_addressFragment_to_addressDetailsFragment)
        }

    }

    private fun setupViewModel() {
        val factory = SettingViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]
    }

    private fun setupAdapters() {
        addressAdapter = AddressAdapter { addressId, default ->
            // Check if it's the last address before deletion
            if (addressAdapter.itemCount > 1 && !default ) {
                settingViewModel.deleteAddress(customerId,addressId)
            } else {
                showConfirmationDialog()
            }
        }
        binding.recyclerViewAddresses.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewAddresses.adapter = addressAdapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            settingViewModel.addressState.collect { uiState ->
                when (uiState) {
                    is ApiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recyclerViewAddresses.visibility = View.GONE
                    }
                    is ApiState.Success<*> -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerViewAddresses.visibility = View.VISIBLE
                        val addresses = uiState.data as List<Address>
                        addressAdapter.submitList(addresses)
                    }
                    is ApiState.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recyclerViewAddresses.visibility = View.GONE
                        Log.d("setupObservers", "setupObservers:${uiState.message} ")
                        Toast.makeText(requireContext(), "Error: ${uiState.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showConfirmationDialog() {
        // Create and show the confirmation dialog
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Update Address")
        dialogBuilder.setMessage("You cannot delete the default address.you must add another one before delete")

        dialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // User wants to update the address
            findNavController().navigate(R.id.mapsFragment)
            dialog.dismiss()
        }

        dialogBuilder.setNegativeButton("No") { dialog, _ ->
            // User does not want to update the address
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        settingViewModel.loadAddresses(customerId)
    }
}