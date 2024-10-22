package com.example.mcommerce.ui.setting.view

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mcommerce.MainActivity
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentSettingBinding



import com.example.mcommerce.my_key.MyKey

import com.example.mcommerce.ui.setting.view.currency.CurrencyDialogFragment
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment(), CurrencyDialogFragment.CurrencySelectionListener {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferences2: SharedPreferences
    private lateinit var imageList: List<SlideModel>
    lateinit var mAuth: FirebaseAuth
    var guest: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        sharedPreferences2 =
            requireActivity().getSharedPreferences(
                MyKey.MY_SHARED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        loadCurrency()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
        guest = sharedPreferences.getString(MyKey.GUEST, "login")

        // setupImageSlider()
    }

//    private fun setupImageSlider() {
//        imageList = listOf(
//            SlideModel("https://www.picng.com/upload/spider_man/png_spider_man_76928.png", "Cobon25%"),
//            SlideModel("https://w7.pngwing.com/pngs/915/345/png-transparent-multicolored-balloons-illustration-balloon-balloon-free-balloons-easter-egg-desktop-wallpaper-party-thumbnail.png", "Cobon50%"),
//            SlideModel("https://www.picng.com/upload/spider_man/png_spider_man_76928.png", "mohamed"),
//            SlideModel("https://w7.pngwing.com/pngs/915/345/png-transparent-multicolored-balloons-illustration-balloon-balloon-free-balloons-easter-egg-desktop-wallpaper-party-thumbnail.png", "Cobon60%"),
//            SlideModel("https://w7.pngwing.com/pngs/915/345/png-transparent-multicolored-balloons-illustration-balloon-balloon-free-balloons-easter-egg-desktop-wallpaper-party-thumbnail.png", "Cobon100%")
//        )
//
//        binding.imageSlider.setImageList(imageList)
//        binding.imageSlider.setItemClickListener(object : ItemClickListener {
//            override fun doubleClick(position: Int) {}
//
//            override fun onItemSelected(position: Int) {
//                imageList[position].title?.let { copyToClipboard(it) }
//            }
//        })
//    }

    private fun setupOnClickListeners() {
        binding.addressSection.setOnClickListener {
            if (guest != "GUEST") {
                navigateTo(R.id.action_settingFragment_to_addressFragment)
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Regester")
                    .setMessage("if you want to see the address you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

        }
        binding.aboutSection.setOnClickListener {
            navigateTo(R.id.action_settingFragment_to_aboutUsFragment)
        }
        binding.contactSection.setOnClickListener {
            navigateTo(R.id.action_settingFragment_to_contactUsFragment)
        }
        binding.currencySection.setOnClickListener {
            if (guest != "GUEST") {
                showCurrencyDialog()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Regester")
                    .setMessage("if you want to change the currency you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

        }
        binding.ordericon.setOnClickListener {
            val action = SettingFragmentDirections.actionNavigationSettingToOrderFragment()
            findNavController().navigate(action)
        }
        binding.logoutButton.setOnClickListener {
            mAuth = FirebaseAuth.getInstance()
            mAuth.addAuthStateListener { firebaseAuth ->
                if (mAuth.currentUser == null) {
                    context?.let {
                        val intent = Intent(it, MainActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    } ?: Log.e("AccountFragment", "Context is null")
                }
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Do You Want Logout")
                .setPositiveButton("Yes") { dialog, _ ->
                    mAuth.signOut()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun navigateTo(actionId: Int) {
        findNavController().navigate(actionId)
    }

    private fun showCurrencyDialog() {
        CurrencyDialogFragment().show(childFragmentManager, "currencyDialog")
    }

    override fun onCurrencySelected(currency: String) {
        binding.currencyText.text = currency
        saveCurrency(currency)
    }

    private fun saveCurrency(currency: String) {
        with(sharedPreferences.edit()) {
            putString("currency", currency)
            apply()
        }
        Toast.makeText(requireContext(), "Currency saved: $currency", Toast.LENGTH_SHORT).show()
    }

    private fun loadCurrency() {
        val savedCurrency = sharedPreferences.getString("currency", "EGP") ?: "EGP"
        binding.currencyText.text = savedCurrency
    }


//    private fun copyToClipboard(title: String) {
//        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//        val clip = ClipData.newPlainText("Slide Title", title)
//        clipboard.setPrimaryClip(clip)
//        Toast.makeText(requireContext(), "Title copied: $title", Toast.LENGTH_SHORT).show()
//    }
}