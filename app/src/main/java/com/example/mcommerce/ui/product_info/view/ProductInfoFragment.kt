package com.example.mcommerce.ui.product_info.view

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
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.mcommerce.databinding.FragmentProductInfoBinding
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.responses.ProductResponse
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModel
import com.example.mcommerce.ui.product_info.viewmodel.ProductInfoViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

class ProductInfoFragment : Fragment() {
    lateinit var binding: FragmentProductInfoBinding
    lateinit var productInfoViewModel: ProductInfoViewModel
    lateinit var productInfoViewModelFactory: ProductInfoViewModelFactory
    private val TAG = "ProductInfoFragment"
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
    }

    fun getProductInfoDetails() {
        productInfoViewModel.getProductDetails(9728829063467)
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
        val randomNumber2 = Random.nextInt(0,10)
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

        binding.btnAddToCard.setOnClickListener {
            if (!size.isNullOrBlank()) {
                if (!color.isNullOrBlank()) {
                    // هنا معاك المقاس واللون اعمل الى انت عاوزه
                    // لو عاوز بقا id المنتج كل حاجه عندك as you like
                    Log.d(TAG, "showProductInfoDetails: size = $size  color = $color  price = $price")
                } else {
                    Toast.makeText(requireContext(), "Please Select Color", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(requireContext(), "Please Select Size", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnAddToFavorite.setOnClickListener {
            // هنا هضيف ف الفيفوريت
        }

    }
}