package com.example.mcommerce.ui.setting.view.adress

import android.location.Geocoder
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.mcommerce.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        googleMap.setOnMapClickListener { latLng ->
            getAddress(latLng)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getAddress(latLng: LatLng) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses?.isNotEmpty() == true) {
            val address = addresses[0]
            val city = address.subAdminArea ?: ""
            val country = address.countryName ?: ""
            val fullAddress = address.getAddressLine(0) ?: ""
            Log.i("TAG", "getAddress: $city")

            val bundle = Bundle().apply {
                putString("city", city)
                putString("country", country)
                putString("address", fullAddress)
            }

            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.mapsFragment, true)
                .build()

            findNavController().navigate(R.id.addressDetailsFragment, bundle, navOptions)
        } else {
            Toast.makeText(requireContext(), "No address found", Toast.LENGTH_SHORT).show()
        }
    }
}