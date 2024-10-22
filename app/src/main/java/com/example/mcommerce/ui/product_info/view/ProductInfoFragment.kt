package com.example.mcommerce.ui.product_info.view



import android.content.Context
import android.content.SharedPreferences

import android.app.AlertDialog

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentProductInfoBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    var favoriteDraftOrderId: Long = 0



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


        favoriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteDataSource(ProductInfoRetrofit.productService)
            )
        )
        favoriteViewModel =
            ViewModelProvider(this, favoriteViewModelFactory).get(FavoriteViewModel::class.java)
        favoriteDraftOrderId =
            (sharedPreferences.getString(MyKey.MY_FAVORITE_DRAFT_ID, "0") ?: "0").toLong()
        draftOrderID =( sharedPreferences.getString(MyKey.MY_CARD_DRAFT_ID, "1")?: "1").toLong()
        Log.d(TAG, "onViewCreated: $favoriteDraftOrderId")
        Log.d("draftOrderID", "draftOrderID: $draftOrderID")
        getDraftOrderById(favoriteDraftOrderId)
        getDraftOrderByIdForCart(draftOrderID)

        binding.btnAddToFavorite.setOnClickListener {
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
        }
        binding.btnAddToCard.setOnClickListener {
                    lifecycleScope.launch(Dispatchers.IO) {
                                    var oldLineItem: MutableList<LineItem> = mutableListOf()

                                    draftOrderRequestForCart.draft_order.line_items.forEach {
                                        oldLineItem.add(it)
                                    }
                                    val draftOrder =  draftOrderRequest(products).draft_order.line_items.get(0)
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
                            }
                        }


    fun getDraftOrderByIdForCart(draftOrderId: Long){
        lifecycleScope.launch {
            productInfoViewModel.getFavoriteDraftOrder(draftOrderId)
            productInfoViewModel.draftOrderStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {
                        Log.d(TAG, "showProductInfoDetails: btnAddToCard ${it.message}")
                    }

                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        draftOrderRequestForCart = DraftOrderRequest( it.data.draft_order)

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
        var color: String? = null
        var size: String? = null
        var price: String? = null
        val randomRatingBarList = listOf(2.5f, 3.5f, 4.0f, 4.5f, 5.0f, 1.0f, 1.5f, 2.0f, 3.0f, 4.2f)
        val randomNumber = Random.nextInt(1, 10)
        binding.tvTitle.text = products.title
        binding.tvDescription.text = products.body_html
        price = products.variants.get(0).price
        binding.tvPrice.text = price + " EGP"
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
      /*  binding.btnAddToCard.setOnClickListener {
            Log.d("draftOrderID", "draftOrderID after click: $draftOrderID")
            if (!size.isNullOrBlank()) {
                if (!color.isNullOrBlank()) {
                    favoriteViewModel.getFavoriteDraftOrder(draftOrderID)
                    Log.d("draftOrderID", "draftOrderID after gir favorite: $draftOrderID")
                //    productInfoViewModel.insertItemToDraftOrder( draftOrderID,draftOrderRequest(products))
                    lifecycleScope.launch {
                        Log.d("draftOrderID", "draftOrderID at launch: $draftOrderID")
                       /* if (draftOrderID != 1172999471403) {
                            favoriteViewModel.createFavoriteDraftOrder(draftOrderRequest(products))
                            delay(2000)
                            favoriteViewModel.getAllFavoriteDraftOrders()
                            favoriteViewModel.allDraftOrdersStateFlow.collectLatest {
                                when (it) {
                                    is ApiState.Failure -> {}
                                    is ApiState.Loading -> {}
                                    is ApiState.Success -> {
                                        Log.d(
                                            TAG,
                                            "onClick: ana defto hala ${it.data.get(it.data.size - 1).id}"
                                        )
                                        sharedPreferences.edit().putString(
                                            MyKey.DRAFT_ORDER_CART_ID,
                                            "${it.data.get(it.data.size - 1).id}"
                                        ).apply()
                                    }
                                }
                            }
                        }*/

                            favoriteViewModel.draftOrderStateFlow.collectLatest {
                                when (it) {
                                    is ApiState.Failure -> {}
                                    is ApiState.Loading -> {}
                                    is ApiState.Success -> {
                                        var oldLineItem: MutableList<LineItem> = mutableListOf()
                                        it.data.draft_order.line_items.forEach {
                                            oldLineItem.add(it)
                                            Log.d("draftOrderID", "oldLineItem: $oldLineItem")
                                            Log.d("draftOrderID", "draftOrderID when add: $draftOrderID")
                                        }
                                        oldLineItem.add(

                                            draftOrderRequest(products).draft_order.line_items.get(0)


                                        )
                                        Log.d("draftOrderID", "oldLineItem after add: $oldLineItem")
                                        val draft = draftOrderRequest(products).draft_order

                                        draft.line_items = oldLineItem

                                        favoriteViewModel.updateFavoriteDraftOrder(
                                            draftOrderID,
                                            UpdateDraftOrderRequest(draft)
                                        )
                                        Log.d("draftOrderID", "draftOrderID after update : $draftOrderID")
//                                }
//                                else{
//                                    favoriteViewModel.createOrder(draftOrderRequest(products))
//                                }


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
                Toast.makeText(requireContext(), "Please Select Size", Toast.LENGTH_SHORT).show()
            }
        }*/
   // }

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

}

