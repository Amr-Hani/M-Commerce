package com.example.mcommerce.ui.product_info.view


import android.content.Context
import android.content.SharedPreferences

import android.app.AlertDialog
import android.content.Intent

import android.graphics.Color

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

import com.example.mcommerce.R

import com.example.mcommerce.MainActivity

import com.example.mcommerce.databinding.FragmentProductInfoBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.network.currency.RetrofitInstance
import com.example.mcommerce.model.pojos.AppliedDiscount
import com.example.mcommerce.model.pojos.Customers
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest

import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModel
import com.example.mcommerce.ui.favorite.viewmodel.FavoriteViewModelFactory

import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModel
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModelFactory
import com.example.mcommerceapp.model.network.RemoteDataSourceForCurrency
import com.example.mcommerceapp.model.network.Repo
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModel
import com.example.mcommerceapp.ui.setting.veiwmodel.SettingViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

class ProductInfoFragment : Fragment() {


    lateinit var binding: FragmentProductInfoBinding

    lateinit var productInfoViewModel: ProductInfoViewModel
    lateinit var productInfoViewModelFactory: ProductInfoViewModelFactory

    lateinit var sharedPreferences: SharedPreferences
    var draftOrderID: Long = 0
    private val TAG = "ProductInfoFragment"
    lateinit var favoriteViewModel: FavoriteViewModel
    lateinit var favoriteViewModelFactory: FavoriteViewModelFactory

    lateinit var draftOrderRequest: DraftOrderRequest
    lateinit var draftOrderRequestForCart: DraftOrderRequest
    lateinit var products: Products
    var customerId: Long = 0
    var isFavorite = false
    var productId: Long = 0
    var rating: Double = 0.0
    var favoriteDraftOrderId: Long = 0
    lateinit var currencySharedPreferences: SharedPreferences

    var color: String? = null
    var size: String? = null
    var price: String? = null
    var guest: String? = null
    var currency: String? = null
    private val settingViewModel: SettingViewModel by viewModels {
        SettingViewModelFactory(
            Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService)),
            Repo.getInstance(RemoteDataSourceForCurrency(RetrofitInstance.exchangeRateApi))
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currencySharedPreferences =
            requireActivity().getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        currency = currencySharedPreferences.getString("currency", "EGP") ?: "EGP"

        productId = ProductInfoFragmentArgs.fromBundle(requireArguments()).productId
        productInfoViewModelFactory = ProductInfoViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        productInfoViewModel = ViewModelProvider(
            this,
            productInfoViewModelFactory
        ).get(ProductInfoViewModel::class.java)
        getProductInfoDetails()

        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        draftOrderID = (sharedPreferences.getString(MyKey.MY_CARD_DRAFT_ID, "1")
            ?: "1").toLong()
        Log.d(TAG, "onViewCreated: draftOrderID ${draftOrderID}")

        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)
        favoriteDraftOrderId =
            (sharedPreferences.getString(MyKey.MY_FAVORITE_DRAFT_ID, "0") ?: "0").toLong()
        Log.d(TAG, "onViewCreated: favoriteDraftOrderId$favoriteDraftOrderId")
        getDraftOrderById(favoriteDraftOrderId)
        getDraftOrderByIdForCart(draftOrderID)

        guest = sharedPreferences.getString(MyKey.GUEST, "login")
        binding.btnAddToFavorite.setOnClickListener {
            if (guest != "GUEST") {

                if (isFavorite) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Deletion")
                        .setMessage("Do You Want Remove This Item From Favorite")
                        .setPositiveButton("Yes") { dialog, _ ->

                            isFavorite = !isFavorite
                            lifecycleScope.launch {

                                Log.d(TAG, "onViewCreated: ana not favorite ")

                                var oldLineItem: MutableList<LineItem> = mutableListOf()

                                draftOrderRequest.draft_order.line_items.forEach {
                                    if (products.title != it.title) {
                                        oldLineItem.add(it)
                                    }
                                }
                                //oldLineItem.remove(draftOrderRequest(products).draft_order.line_items.get(0))
                                Log.d(TAG, "onViewCreated: $oldLineItem")
                                val draft = draftOrderRequest.draft_order

                                draft.line_items = oldLineItem
                                binding.btnAddToFavorite.setColorFilter(Color.RED)
                                productInfoViewModel.updateFavoriteDraftOrder(
                                    favoriteDraftOrderId,
                                    UpdateDraftOrderRequest(draft)
                                )
                                binding.btnAddToFavorite.setColorFilter(Color.BLACK)
                            }
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()

                } else {
                    isFavorite = !isFavorite
                    lifecycleScope.launch {

                        Log.d(TAG, "onViewCreated: ana not favorite ")

                        var oldLineItem: MutableList<LineItem> = mutableListOf()

                        draftOrderRequest.draft_order.line_items.forEach {
                            oldLineItem.add(it)
                        }
                        oldLineItem.add(draftOrderRequest(products).draft_order.line_items.get(0))
                        Log.d(TAG, "onViewCreated: $oldLineItem")
                        val draft = draftOrderRequest.draft_order

                        draft.line_items = oldLineItem
                        productInfoViewModel.updateFavoriteDraftOrder(
                            favoriteDraftOrderId,
                            UpdateDraftOrderRequest(draft)
                        )
                        binding.btnAddToFavorite.setColorFilter(Color.RED)
                    }
                }
            } else {

                AlertDialog.Builder(requireContext())
                    .setTitle("Regester")
                    .setMessage("if you want to add to favorite you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        binding.btnAddToCard.setOnClickListener {
            if (guest != "GUEST") {
                if (!size.isNullOrBlank()) {
                    if (!color.isNullOrBlank()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            favoriteViewModel.getFavoriteDraftOrder(draftOrderID)
                            favoriteViewModel.draftOrderStateFlow.collectLatest {
                                when (it) {
                                    is ApiState.Failure -> {
                                        Log.d(
                                            TAG,
                                            "showProductInfoDetails: btnAddToCard ${it.message}"
                                        )
                                    }

                                    is ApiState.Loading -> {}
                                    is ApiState.Success -> {
                                        Log.d(TAG, "showProductInfoDetails: sucess here ")
                                        var oldLineItem: MutableList<LineItem> = mutableListOf()

                                        draftOrderRequest.draft_order.line_items.forEach {
                                            oldLineItem.add(it)
                                        }
                                        val draftOrder =
                                            draftOrderRequest(products).draft_order.line_items.get(0)
                                        oldLineItem.add(
                                            draftOrder
                                        )
                                        Log.d(TAG, "showProductInfoDetails: $draftOrder")
                                        Log.d(TAG, "onViewCreated: $oldLineItem")
                                        val draft = draftOrderRequest.draft_order

                                        draft.line_items = oldLineItem

                                        productInfoViewModel.updateFavoriteDraftOrder(
                                            draftOrderID,
                                            UpdateDraftOrderRequest(draft)
                                        )
                                    }
                                }
                            }
                        }
                        Log.d(
                            TAG,
                            "showProductInfoDetails: size = $size  color = $color  price = $price"
                        )
                    } else {
                        Toast.makeText(requireContext(), "Please Select Color", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please Select Size", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Regester")
                    .setMessage("if you want to add to cart you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        binding.btnAddToCard.setOnClickListener {
            if (guest != "GUEST") {
                if (!size.isNullOrBlank()) {
                    if (!color.isNullOrBlank()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            var oldLineItem: MutableList<LineItem> = mutableListOf()

                            draftOrderRequestForCart.draft_order.line_items.forEach {
                                oldLineItem.add(it)
                            }
                            val draftOrder =
                                draftOrderRequest(products).draft_order.line_items.get(0)
                            oldLineItem.add(
                                draftOrder
                            )
                            Log.d(TAG, "showProductInfoDetails: $draftOrder")
                            Log.d(TAG, "onViewCreated: $oldLineItem")
                            val draft = draftOrderRequest.draft_order

                            draft.line_items = oldLineItem

                            favoriteViewModel.updateFavoriteDraftOrder(
                                draftOrderID,
                                UpdateDraftOrderRequest(draft)
                            )
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Please Select Color",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please Select Size", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Regester")
                    .setMessage("if you want to add to cart you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    fun getDraftOrderByIdForCart(draftOrderId: Long) {
        lifecycleScope.launch {
            productInfoViewModel.getFavoriteDraftOrder(draftOrderId)
            productInfoViewModel.draftOrderStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {
                        Log.d(TAG, "showProductInfoDetails: btnAddToCard ${it.message}")
                    }

                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        draftOrderRequestForCart = DraftOrderRequest(it.data.draft_order)

                    }
                }
            }
        }
    }

    fun getDraftOrderById(favoriteDraftOrderId: Long) {
        lifecycleScope.launch {
            productInfoViewModel.getFavoriteDraftOrder(favoriteDraftOrderId)
            productInfoViewModel.draftOrderStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {
                        Log.d(TAG, "getDraftOrderById: ${it.message}")
                    }

                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        draftOrderRequest = DraftOrderRequest(it.data.draft_order)
                        Log.d(TAG, "getDraftOrderById: $draftOrderRequest")

                        it.data.draft_order.line_items.forEach {
                            if (it.title == products.title) {
                                isFavorite = true
                            }
                        }
                        Log.d(TAG, "getDraftOrderById: $isFavorite")
                        if (isFavorite) {
                            binding.btnAddToFavorite.setColorFilter(Color.RED)
                        } else {
                            binding.btnAddToFavorite.setColorFilter(Color.BLACK)
                        }
                    }
                }
            }
        }


    }

    fun getProductInfoDetails() {
        productInfoViewModel.getProductDetails(productId)
        lifecycleScope.launch {
            productInfoViewModel.productDetailsStateFlow.collectLatest {
                when (it) {
                    is ApiState.Loading -> {
                        Log.e(TAG, "getProductInfoDetails: loading")
                    }

                    is ApiState.Failure -> {
                        Log.d(TAG, "getProductInfoDetails: faillllllllllllllllllll")
                    }

                    is ApiState.Success -> {
                        Log.d(TAG, "getProductInfoDetails: Successssssssssssssssss")
                        products = it.data.get(0)
                        showProductInfoDetails(it.data.get(0))
                    }
                }
            }
        }
    }


    fun showProductInfoDetails(products: Products) {

        if (currency == "EGP") {
            price = products.variants.get(0).price
            binding.tvPrice.text = price + " EGP"
        } else {
            getRatCurrency()
            price = products.variants.get(0).price
            val totalPrice = (price!!.toDouble() / rating)
            //val formattedPrice = BigDecimal(totalPrice).setScale(2, RoundingMode.HALF_UP).toDouble()
            val formattedPrice = String.format("%.2f", totalPrice)
            Thread.sleep(250)
            binding.tvPrice.text =  "$formattedPrice USD"
        }
        val randomRatingBarList =
            listOf(2.5f, 3.5f, 4.0f, 4.5f, 5.0f, 1.0f, 1.5f, 2.0f, 3.0f, 4.2f)
        val randomNumber = Random.nextInt(1, 10)
        binding.tvTitle.text = products.title
        binding.tvDescription.text = products.body_html

        binding.ratingBar.rating = randomRatingBarList.get(randomNumber)
        Log.d(TAG, "showProductInfoDetails: ${products.body_html}")
        val showImagesProducts = products.images.map {
            SlideModel(
                it.src, "",
                ScaleTypes.FIT
            )
        }
        binding.sliderImage.setImageList(showImagesProducts)

        val randomReviewList = listOf(
            "Excellent product, highly recommend!",
            "Good quality but could be improved.",
            "Not bad, but not the best I've seen.",
            "Amazing! Exceeded my expectations.",
            "Wouldn't recommend, not worth the price.",
            "Decent for the cost, but has flaws.",
            "Great value for money!",
            "Poor packaging, product damaged.",
            "The product does what it promises.",
            "Will definitely buy again!"
        )

        val randomArabicNamesList = listOf(
            "Ahmed Khaled",
            "Mohamed Ali",
            "Sara Youssef",
            "Yasmin Ibrahim",
            "Omar Hassan",
            "Layla Mahmoud",
            "Hanan Mostafa",
            "Tarek Nasser",
            "Rami Abdullah",
            "Amina Fathi"
        )

        binding.rbReview.rating = randomRatingBarList.get(randomNumber)
        binding.tvReviewDesc.text = randomReviewList.get(randomNumber)
        binding.tvReviewName.text = randomArabicNamesList.get(randomNumber)
        val randomNumber2 = Random.nextInt(0, 10)
        binding.rbReview2.rating = randomRatingBarList.get(randomNumber2)
        binding.tvReviewDesc2.text = randomReviewList.get(randomNumber2)
        binding.tvReviewName2.text = randomArabicNamesList.get(randomNumber2)
        products.options.forEach {
            when (it.name) {
                "Size" -> {
                    val options = it.values
                    options.forEachIndexed { index, option ->
                        val radioButton = RadioButton(requireContext()).apply {
                            id = View.generateViewId()
                            text = option
                            layoutParams = RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 16, 0)
                            }
                            setPadding(16, 8, 16, 8)
                            textSize = 14f


                        }
                        binding.radioGroup.addView(radioButton)
                    }

                    binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
                        val selectedRadioButton =
                            binding.radioGroup.findViewById<RadioButton>(checkedId)
                        selectedRadioButton?.let {
                            it. setTextColor(Color.BLACK)
                            Toast.makeText(
                                requireContext(),
                                "Select into ${it.text}",
                                Toast.LENGTH_SHORT
                            ).show()
                            size = it.text.toString()
                        }
                    }
                }

                "Color" -> {
                    val options = it.values
                    options.forEachIndexed { index, option ->
                        val radioButton = RadioButton(requireContext()).apply {
                            id = View.generateViewId()
                            text = option
                            layoutParams = RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 16, 0)
                            }
                            setPadding(16, 8, 16, 8)
                            textSize = 14f
                            setTextColor(Color.BLACK)
                        }
                        binding.radioGroup2.addView(radioButton)
                    }
                    binding.radioGroup2.setOnCheckedChangeListener { _, checkedId ->
                        val selectedRadioButton =
                            binding.radioGroup2.findViewById<RadioButton>(checkedId)
                        selectedRadioButton?.let {
                            color = it.text.toString()
                            Toast.makeText(
                                requireContext(),
                                "Select into ${it.text}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    }

    private fun draftOrderRequest(products: Products): DraftOrderRequest {
        val draftOrderRequest = DraftOrderRequest(
            draft_order = DraftOrder(
                line_items = listOf(
                    LineItem(
                        product_id = products.id,
                        sku = "${products.id}<+>${products.image?.src}",
                        title = products.title, price = products.variants[0].price, quantity = 1
                    )
                ),
                use_customer_default_address = true,
                applied_discount = AppliedDiscount(),
                customer = Customers(customerId)
            )

        )
        return draftOrderRequest
    }

    fun getRatCurrency(){
        viewLifecycleOwner.lifecycleScope.launch {
            settingViewModel.fetchLatestRates()
            settingViewModel.exchangeRatesState.collectLatest { state ->
                when (state) {
                    is ApiState.Loading -> {

                    }
                    is ApiState.Success -> {

                        rating = state.data
                    }
                    is ApiState.Failure -> {
                        Log.e("CartFragment", "Error fetching rates:")
                    }
                }
            }
        }
    }

}

