package com.example.mcommerce.ui.setting.view.adress

import android.content.Context
import android.content.SharedPreferences
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
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentAddressDetailsBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.network.currency.RetrofitInstance
import com.example.mcommerce.model.responses.Address
import com.example.mcommerce.model.responses.address.CustomerAddress
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerceapp.model.network.IRepo
import com.example.mcommerceapp.model.network.RemoteDataSourceForCurrency
import com.example.mcommerceapp.model.network.Repo
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressDetailsFragment : Fragment() {

    private lateinit var binding: FragmentAddressDetailsBinding
    private lateinit var settingViewModel: SettingViewModel
    //lateinit var sharedPreferences: SharedPreferences
    val customerId = 8246104654123
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressDetailsBinding.inflate(inflater, container, false)

        setupViewModel()
        observeViewModel()


      //  sharedPreferences = requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
      //  customerId = (sharedPreferences.getString(MyKey.MY_CUSTOMER_ID, "0") ?: "0").toLong()
        arguments?.let { bundle ->
            val city = bundle.getString("city")
            val country = bundle.getString("country")
            val address = bundle.getString("address")

            binding.editTextCity.setText(city)
            binding.editTextCountry.setText(country)
            binding.editTextAddress.setText(address)
        }

        binding.buttonAddAddress.setOnClickListener {
            saveAddressData()
        }

        binding.imageButtonNavigateToMap.setOnClickListener {
            findNavController().navigate(R.id.action_addressDetailsFragment_to_mapsFragment)
        }

        return binding.root
    }

    private fun setupViewModel() {
        val factory = SettingViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            ), Repo.getInstance(RemoteDataSourceForCurrency(RetrofitInstance.exchangeRateApi))
        )
        settingViewModel = ViewModelProvider(this, factory)[SettingViewModel::class.java]
    }

    private fun observeViewModel() {
        // Collect StateFlow in a lifecycle-aware way
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.addressState.collectLatest { state ->
                handleState(state)
            }
        }
    }

    private fun handleState(state: ApiState<List<Address>>) {
        when (state) {

            is ApiState.Loading -> {
                Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()
            }
            is ApiState.Success -> {
                Toast.makeText(requireContext(), "Address added successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack(R.id.addressFragment, false)
            }
            is ApiState.Failure -> {
                Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun saveAddressData() {
        val country = binding.editTextCountry.text.toString().trim()
        val city = binding.editTextCity.text.toString().trim()
        val address = binding.editTextAddress.text.toString().trim()
        val phone = binding.editTextPhone.text.toString().trim()
        val countryMap = mapOf(
            "Afghanistan" to "AF",
            "Albania" to "AL",
            "Algeria" to "DZ",
            "Andorra" to "AD",
            "Angola" to "AO",
            "Antigua and Barbuda" to "AG",
            "Argentina" to "AR",
            "Armenia" to "AM",
            "Australia" to "AU",
            "Austria" to "AT",
            "Azerbaijan" to "AZ",
            "Bahamas" to "BS",
            "Bahrain" to "BH",
            "Bangladesh" to "BD",
            "Barbados" to "BB",
            "Belarus" to "BY",
            "Belgium" to "BE",
            "Belize" to "BZ",
            "Benin" to "BJ",
            "Bhutan" to "BT",
            "Bolivia" to "BO",
            "Bosnia and Herzegovina" to "BA",
            "Botswana" to "BW",
            "Brazil" to "BR",
            "Brunei" to "BN",
            "Bulgaria" to "BG",
            "Burkina Faso" to "BF",
            "Burundi" to "BI",
            "Cabo Verde" to "CV",
            "Cambodia" to "KH",
            "Cameroon" to "CM",
            "Canada" to "CA",
            "Central African Republic" to "CF",
            "Chad" to "TD",
            "Chile" to "CL",
            "China" to "CN",
            "Colombia" to "CO",
            "Comoros" to "KM",
            "Congo, Republic of the" to "CG",
            "Congo, Democratic Republic of the" to "CD",
            "Costa Rica" to "CR",
            "Croatia" to "HR",
            "Cuba" to "CU",
            "Cyprus" to "CY",
            "Czech Republic" to "CZ",
            "Denmark" to "DK",
            "Djibouti" to "DJ",
            "Dominica" to "DM",
            "Dominican Republic" to "DO",
            "Ecuador" to "EC",
            "Egypt" to "EG",
            "El Salvador" to "SV",
            "Equatorial Guinea" to "GQ",
            "Eritrea" to "ER",
            "Estonia" to "EE",
            "Eswatini" to "SZ",
            "Ethiopia" to "ET",
            "Fiji" to "FJ",
            "Finland" to "FI",
            "France" to "FR",
            "Gabon" to "GA",
            "Gambia" to "GM",
            "Georgia" to "GE",
            "Germany" to "DE",
            "Ghana" to "GH",
            "Greece" to "GR",
            "Grenada" to "GD",
            "Guatemala" to "GT",
            "Guinea" to "GN",
            "Guinea-Bissau" to "GW",
            "Guyana" to "GY",
            "Haiti" to "HT",
            "Honduras" to "HN",
            "Hungary" to "HU",
            "Iceland" to "IS",
            "India" to "IN",
            "Indonesia" to "ID",
            "Iran" to "IR",
            "Iraq" to "IQ",
            "Ireland" to "IE",
            "Israel" to "IL",
            "Italy" to "IT",
            "Jamaica" to "JM",
            "Japan" to "JP",
            "Jordan" to "JO",
            "Kazakhstan" to "KZ",
            "Kenya" to "KE",
            "Kiribati" to "KI",
            "Korea, North" to "KP",
            "Korea, South" to "KR",
            "Kuwait" to "KW",
            "Kyrgyzstan" to "KG",
            "Laos" to "LA",
            "Latvia" to "LV",
            "Lebanon" to "LB",
            "Lesotho" to "LS",
            "Liberia" to "LR",
            "Libya" to "LY",
            "Liechtenstein" to "LI",
            "Lithuania" to "LT",
            "Luxembourg" to "LU",
            "Madagascar" to "MG",
            "Malawi" to "MW",
            "Malaysia" to "MY",
            "Maldives" to "MV",
            "Mali" to "ML",
            "Malta" to "MT",
            "Marshall Islands" to "MH",
            "Mauritania" to "MR",
            "Mauritius" to "MU",
            "Mexico" to "MX",
            "Micronesia" to "FM",
            "Moldova" to "MD",
            "Monaco" to "MC",
            "Mongolia" to "MN",
            "Montenegro" to "ME",
            "Morocco" to "MA",
            "Mozambique" to "MZ",
            "Myanmar" to "MM",
            "Namibia" to "NA",
            "Nauru" to "NR",
            "Nepal" to "NP",
            "Netherlands" to "NL",
            "New Zealand" to "NZ",
            "Nicaragua" to "NI",
            "Niger" to "NE",
            "Nigeria" to "NG",
            "North Macedonia" to "MK",
            "Norway" to "NO",
            "Oman" to "OM",
            "Pakistan" to "PK",
            "Palau" to "PW",
            "Palestine" to "PS",
            "Panama" to "PA",
            "Papua New Guinea" to "PG",
            "Paraguay" to "PY",
            "Peru" to "PE",
            "Philippines" to "PH",
            "Poland" to "PL",
            "Portugal" to "PT",
            "Qatar" to "QA",
            "Romania" to "RO",
            "Russia" to "RU",
            "Rwanda" to "RW",
            "Saint Kitts and Nevis" to "KN",
            "Saint Lucia" to "LC",
            "Saint Vincent and the Grenadines" to "VC",
            "Samoa" to "WS",
            "San Marino" to "SM",
            "Sao Tome and Principe" to "ST",
            "Saudi Arabia" to "SA",
            "Senegal" to "SN",
            "Serbia" to "RS",
            "Seychelles" to "SC",
            "Sierra Leone" to "SL",
            "Singapore" to "SG",
            "Slovakia" to "SK",
            "Slovenia" to "SI",
            "Solomon Islands" to "SB",
            "Somalia" to "SO",
            "South Africa" to "ZA",
            "South Sudan" to "SS",
            "Spain" to "ES",
            "Sri Lanka" to "LK",
            "Sudan" to "SD",
            "Suriname" to "SR",
            "Sweden" to "SE",
            "Switzerland" to "CH",
            "Syria" to "SY",
            "Taiwan" to "TW",
            "Tajikistan" to "TJ",
            "Tanzania" to "TZ",
            "Thailand" to "TH",
            "Togo" to "TG",
            "Tonga" to "TO",
            "Trinidad and Tobago" to "TT",
            "Tunisia" to "TN",
            "Turkey" to "TR",
            "Turkmenistan" to "TM",
            "Tuvalu" to "TV",
            "Uganda" to "UG",
            "Ukraine" to "UA",
            "United Arab Emirates" to "AE",
            "United Kingdom" to "GB",
            "United States" to "US",
            "Uruguay" to "UY",
            "Uzbekistan" to "UZ",
            "Vanuatu" to "VU",
            "Vatican City" to "VA",
            "Venezuela" to "VE",
            "Vietnam" to "VN",
            "Yemen" to "YE",
            "Zambia" to "ZM",
            "Zimbabwe" to "ZW"
        )

        when {
            !isValidCountryOrCity(country) -> {
                binding.editTextCountry.error = "Enter a valid country name"
                return
            }
            !isValidCountryOrCity(city) -> {
                binding.editTextCity.error = "Enter a valid city name"
                return
            }
            !isValidAddress(address) -> {
                binding.editTextAddress.error = "Enter a valid address"
                return
            }
            !isValidPhone(phone) -> {
                binding.editTextPhone.error = "Enter a valid phone number"
                return
            }
            else -> {
                val countryCode = countryMap[country]
                if (countryCode == null) {
                    binding.editTextCountry.error = "Country code not found"
                    return
                }
                val newAddress = CustomerAddress(
                    id = 0,  // This might be 0 or a default value as ID will be generated by the API
                    customer_id = customerId,   // Customer ID
                    first_name = "Mohamed",        // Optional first name
                    last_name = "Alaa",            // Optional last name
                    company = "",                   // Optional company name
                    address1 = address,      // Required primary address line
                    address2 = "",                  // Optional secondary address line
                    city = city,                 // Required city name
                    province = "Cairo",             // Required province/state name
                    country = country,              // Required country name
                    zip = "12345",                  // Required postal/zip code
                    phone = phone,           // Optional phone number
                    name = "Mohamed Alaa",          // Optional full name
                    province_code = "",             // Optional province code
                    country_code = countryCode,            // Required country code
                    country_name = country,         // Required country name
                    default = true                 // Indicates this is not the default address
                )
                settingViewModel.addAddress(customerId, newAddress)
                Log.d("response", "saveAddressData: {$customerId} ")
            }
        }
    }

    private fun isValidCountryOrCity(input: String): Boolean {
        return input.length > 1 && !input.matches(Regex("^[a-zA-Z]{1}$"))
    }

    private fun isValidAddress(address: String): Boolean {
        return address.length > 3
    }
    private fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^01[0125][0-9]{8}$"))
    }
}