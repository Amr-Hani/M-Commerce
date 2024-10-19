package com.example.mcommerce.ui.setting.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentSettingBinding
import com.example.mcommerce.ui.setting.view.currency.CurrencyDialogFragment

class SettingFragment : Fragment(), CurrencyDialogFragment.CurrencySelectionListener {

    private lateinit var binding: FragmentSettingBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var imageList: List<SlideModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences =
            requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        loadCurrency()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
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
            navigateTo(R.id.action_settingFragment_to_addressFragment)
        }
        binding.aboutSection.setOnClickListener {
            navigateTo(R.id.action_settingFragment_to_aboutUsFragment)
        }
        binding.contactSection.setOnClickListener {
            navigateTo(R.id.action_settingFragment_to_contactUsFragment)
        }
        binding.currencySection.setOnClickListener {
            showCurrencyDialog()
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