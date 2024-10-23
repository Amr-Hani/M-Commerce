package com.example.mcommerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AboutUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutUsFragment : Fragment(R.layout.fragment_about_us) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutTextView: TextView = view.findViewById(R.id.about_text)

        // Updated text for an E-commerce app
        aboutTextView.text = """
            Welcome to ShopEase - Your Ultimate E-commerce Experience!
            
            Version 1.0
            
            ShopEase allows you to browse and purchase your favorite products with ease. 
            We offer a wide variety of items from top brands and exclusive deals tailored just for you. 
            
            App by [ITI Android Team].
            
            Enjoy seamless shopping, fast deliveries, and secure payments with our trusted service.
        """.trimIndent()
    }
}