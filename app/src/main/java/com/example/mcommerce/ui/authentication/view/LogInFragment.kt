package com.example.mcommerce.ui.authentication.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.mcommerce.MainActivity2
import com.example.mcommerce.databinding.FragmentLogInBinding
import com.example.mcommerce.model.firebase.FireBaseDataSource
import com.example.mcommerce.model.firebase.Repo
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Address
import com.example.mcommerce.model.pojos.AppliedDiscount
import com.example.mcommerce.model.pojos.Customer
import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.pojos.CustomerUpdate
import com.example.mcommerce.model.pojos.Customers
import com.example.mcommerce.model.pojos.DraftOrder
import com.example.mcommerce.model.pojos.DraftOrderRequest
import com.example.mcommerce.model.pojos.LineItem
import com.example.mcommerce.model.pojos.Products
import com.example.mcommerce.model.pojos.UpdateCustomerRequest
import com.example.mcommerce.model.pojos.UpdateDraftOrderRequest
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModel
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {
    lateinit var binding: FragmentLogInBinding

    lateinit var sharedPreferences: SharedPreferences
    lateinit var authenticationViewModel: AuthenticationViewModel
    lateinit var authenticationViewModelFactory: AuthenticationViewModelFactory
    lateinit var mAuth: FirebaseAuth
    var customerId: Long = 0
    var favoriteDraftOrderId: Long = 0
    var cardDraftOrderId: Long = 0
    private val TAG = "LogInFragment"
    var email: String? = null
    var password: String? = null


    override fun onStart() {
        super.onStart()
        checkIfEmailVerified()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPreferences =
            requireContext().getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        mAuth = FirebaseAuth.getInstance()
        authenticationViewModelFactory = AuthenticationViewModelFactory(
            Repo.getInstance(
                FireBaseDataSource(mAuth)
            ), Repository.getInstance(RemoteDataSource(ProductInfoRetrofit.productService))
        )
        authenticationViewModel = ViewModelProvider(
            this,
            authenticationViewModelFactory
        ).get(AuthenticationViewModel::class.java)

        binding.tvGoToSignUp.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvGoToSignUp.setOnClickListener {
            val action = LogInFragmentDirections.actionLogInFragmentToSignUpFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.btnLogIn.setOnClickListener {
            logIn()
        }
        binding.tvGuest.setOnClickListener {
            sharedPreferences.edit().putString(MyKey.GUEST, "GUEST")
                .apply()
            val intent = Intent(requireActivity(), MainActivity2::class.java)
            startActivity(intent)
        }
    }

    fun logIn() {
        binding.etUserNameLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etUserNameLogin.error = null
                binding.etUserNameLogin.background.setTint(Color.WHITE)

            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPasswordLogin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etPasswordLogin.error = null
                binding.etPasswordLogin.background.setTint(Color.WHITE)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        email = binding.etUserNameLogin.text.toString()
        password = binding.etPasswordLogin.text.toString()
        if (!email.isNullOrEmpty() || !password.isNullOrBlank()) {
            authenticationViewModel.logIn(email!!, password!!)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        checkIfEmailVerified()
                    } else {
                        binding.etPasswordLogin.error = "Maybe Password Is Incorrect"
                        binding.etPasswordLogin.background.setTint(Color.RED)
                        binding.etUserNameLogin.error = "Maybe Email Is Incorrect"
                        binding.etUserNameLogin.background.setTint(Color.RED)
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val exceptionMessage = task.exception?.message ?: "Authentication failed."
                        Log.e("TAG", "signInWithEmailAndPassword: $exceptionMessage")
                    }
                }
        } else {
            Toast.makeText(requireContext(), "Please Fill The Required Fields ", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun checkIfEmailVerified() {
        val user = authenticationViewModel.checkIfEmailVerified()
        if (user != null) {
            if (user.isEmailVerified) {
                Log.d(TAG, "checkIfEmailVerified: Email is verified")
                Toast.makeText(requireContext(), "Authentication success.", Toast.LENGTH_SHORT)
                    .show()
                email?.let { getCustomerByEmail(it) }

                sharedPreferences.edit().putString(MyKey.GUEST, "LogIn")
                    .apply()

                val intent = Intent(requireActivity(), MainActivity2::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(
                    requireContext(),
                    "checkIfEmailVerified: Email is not verified",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "checkIfEmailVerified: Email is not verified. Please verify your email")
            }
        } else {
            Log.e(TAG, "checkIfEmailVerified: User not logged in")
        }
    }

    fun getCustomerByEmail(customerEmail: String) {
        lifecycleScope.launch {
            authenticationViewModel.getCustomerByEmail(customerEmail)
            authenticationViewModel.customerEmailResponseDetailsStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {}
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {
                        if (!it.data.customers.isNullOrEmpty()) {
                            customerId = it.data.customers.get(0).id
                            sharedPreferences.edit()
                                .putString(MyKey.MY_CUSTOMER_ID, "${it.data.customers.get(0).id}")
                                .apply()
                            Log.d(
                                TAG,
                                "getCustomerByEmail: email ${it.data.customers.get(0).email} id = ${
                                    it.data.customers.get(0).id
                                }"
                            )
                            if (it.data.customers.get(0).first_name.isNullOrBlank() && it.data.customers.get(
                                    0
                                ).last_name.isNullOrBlank()
                            ) {
                                createdCardDraftOrder()
                                delay(3000)
                                createdFavoriteDraftOrder()
                                delay(2000)



                                updateCustomer()
                            } else {

                                cardDraftOrderId = it.data.customers.get(0).last_name.toLong()
                                Log.d(TAG, "getCustomerByEmail: cardDraftOrderId $cardDraftOrderId")

                                sharedPreferences.edit().putString(
                                    MyKey.MY_CARD_DRAFT_ID,
                                    "${cardDraftOrderId}"
                                ).apply()

                                favoriteDraftOrderId = it.data.customers.get(0).first_name.toLong()
                                Log.d(
                                    TAG,
                                    "getCustomerByEmail: favoriteDraftOrderId $favoriteDraftOrderId"
                                )
                                sharedPreferences.edit().putString(
                                    MyKey.MY_FAVORITE_DRAFT_ID,
                                    "${favoriteDraftOrderId}"
                                ).apply()

                            }

                        } else {
                            Log.d(TAG, "getCustomerByEmail: this list is null")

                        }

                    }
                }
            }
        }
    }

    fun createdCardDraftOrder() {
        lifecycleScope.launch {
            authenticationViewModel.createFavoriteDraftOrder(createdDraftOrderRequest())
            authenticationViewModel.getCardDraftOrders()
            authenticationViewModel.cardDraftOrdersStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {}
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {

                        cardDraftOrderId = it.data.get(it.data.size - 1).id
                        Log.d(
                            TAG,
                            "cardDraftOrderId ${cardDraftOrderId}"
                        )
                        sharedPreferences.edit().putString(
                            MyKey.MY_CARD_DRAFT_ID,
                            "${cardDraftOrderId}"
                        ).apply()
                    }
                }
            }
        }
    }

    fun createdFavoriteDraftOrder() {
        lifecycleScope.launch {
            authenticationViewModel.createFavoriteDraftOrder(
                createdDraftOrderRequest()
            )
            authenticationViewModel.getAllFavoriteDraftOrders()
            authenticationViewModel.allDraftOrdersStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {}
                    is ApiState.Loading -> {}
                    is ApiState.Success -> {

                        favoriteDraftOrderId = it.data.get(it.data.size - 1).id
                        Log.d(
                            TAG,
                            "favoriteDraftOrderId ${favoriteDraftOrderId}"
                        )
                        sharedPreferences.edit().putString(
                            MyKey.MY_FAVORITE_DRAFT_ID,
                            "${favoriteDraftOrderId}"
                        ).apply()
                    }
                }
            }
        }
    }

    fun updateCustomer() {
        val updateCustomerRequest = UpdateCustomerRequest(
            customer = CustomerUpdate(
                id = customerId,
                first_name = favoriteDraftOrderId.toString(),
                last_name = cardDraftOrderId.toString()
            )
        )
        authenticationViewModel.updateCustomerById(
            customerId, updateCustomerRequest
        )
    }

    private fun createdDraftOrderRequest(): DraftOrderRequest {
        val draftOrderRequest = DraftOrderRequest(
            draft_order = DraftOrder(
                line_items = listOf(
                    LineItem(
                        product_id = 0L,
                        sku = "null",
                        title = "amr", price = "0.0", quantity = 1
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