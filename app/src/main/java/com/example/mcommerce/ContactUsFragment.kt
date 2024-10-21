package com.example.mcommerce

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ContactUsFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val phoneTextView: TextView = view.findViewById(R.id.contact_phone)
        val emailTextView: TextView = view.findViewById(R.id.contact_email)


        phoneTextView.text = "+1 (123) 456-7890"
        emailTextView.text = "contact@example.com"
    }
}