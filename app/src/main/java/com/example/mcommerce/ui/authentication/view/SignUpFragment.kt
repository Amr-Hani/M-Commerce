package com.example.mcommerce.ui.authentication.view

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.mcommerce.databinding.FragmentSignUpBinding
import com.example.mcommerce.model.firebase.FireBaseDataSource
import com.example.mcommerce.model.firebase.Repo
import com.example.mcommerce.model.network.ApiState
import com.example.mcommerce.model.network.ProductInfoRetrofit
import com.example.mcommerce.model.network.RemoteDataSource
import com.example.mcommerce.model.network.Repository
import com.example.mcommerce.model.pojos.Address
import com.example.mcommerce.model.pojos.Customer
import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModel
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SignUpFragment : Fragment() {
    lateinit var binding: FragmentSignUpBinding
    lateinit var sharedPreferences: SharedPreferences
    lateinit var authenticationViewModel: AuthenticationViewModel
    lateinit var authenticationViewModelFactory: AuthenticationViewModelFactory
    lateinit var mAuth: FirebaseAuth
    private val TAG = "SignUpFragment"

    val customer = Customer(
        first_name = "",
        last_name = "",
        email = "",
        phone = "",
        verified_email = true,
        addresses = listOf(
            Address(
            address1 = "",
            city = "",
            province = "",
            phone = "",
            zip = "",
            last_name = "",
            first_name = "",
            country = ""
        )
        ),
        password = "",
        password_confirmation = "",
        send_email_welcome = false
    )
    val customerRequest = CustomerRequest(customer)

    lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
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
        binding.btnSignUp.setOnClickListener {
            register()
        }
        binding.tvGoToLogin.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.tvGoToLogin.setOnClickListener {
            val action = SignUpFragmentDirections.actionSignUpFragmentToLogInFragment()
            Navigation.findNavController(binding.root).navigate(action)
        }
        binding.btnGoogle.setOnClickListener {
        }

        binding.tvGuest.setOnClickListener {
            sharedPreferences.edit().putString(MyKey.GUEST, "Guest")
                .apply()
        }
    }

    fun register() {

        binding.etUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etUserName.error = null
                binding.etUserName.background.setTint(Color.WHITE)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etPassword.error = null
                binding.etPassword.background.setTint(Color.WHITE)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etConfirmPassword.error = null
                binding.etConfirmPassword.background.setTint(Color.WHITE)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        val email = binding.etUserName.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (isEmailValid(email)) {
            if (isPasswordValid(password)) {
                if (isPasswordMatching(password, confirmPassword)) {
                    checkIfEmailExists(email, password)
                } else {
                    binding.etConfirmPassword.error = "Passwords do not match"
                    binding.etConfirmPassword.background.setTint(Color.RED)
                }
            } else {
                binding.etPassword.error =
                    "Invalid Password Must Contain !@#\$%^&*()-_=+{}[]|:;\"'<>,.?/~`"
                binding.etPassword.background.setTint(Color.RED)
            }
        } else {
            binding.etUserName.error = "Invalid Email"
            binding.etUserName.background.setTint(Color.RED)
        }

    }

    private fun checkIfEmailExists(email: String, password: String) {
        mAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        authenticationViewModel.signUp(email, password)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        requireContext(),
                                        "Authentication success.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    customerRequest.customer.email = email
                                    customerRequest.customer.password = password
                                    customerRequest.customer.password_confirmation = password

                                    postCustomer(customerRequest)
                                    val action =
                                        SignUpFragmentDirections.actionSignUpFragmentToLogInFragment()
                                    Navigation.findNavController(binding.root).navigate(action)

                                    authenticationViewModel.sendVerificationEmail(mAuth.currentUser)
                                        ?.addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d(
                                                    TAG,
                                                    "onViewCreated: Verification email sent to $email"
                                                )
                                            } else {
                                                Log.e(TAG, "sendEmailVerification", task.exception)
                                                Log.e(TAG, "Failed to send verification email.")
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "Authentication failed.",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    val exceptionMessage =
                                        task.exception?.message ?: "Authentication failed."
                                    Log.d(TAG, "signInWithEmailAndPassword: $exceptionMessage")
                                }
                            }
                    } else {
                        Log.d(TAG, "checkIfEmailExists: Email already registered")
                    }
                } else {
                    Log.e("TAG", "Error checking email: ${task.exception?.message}")
                    Log.e(TAG, "checkIfEmailExists: Failed to check email")
                }
            }
    }


    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) return false

        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }

        val hasDigit = password.any { it.isDigit() }

        val specialCharacters = "!@#\$%^&*()-_=+{}[]|:;\"'<>,.?/~`"
        val hasSpecialChar = password.any { it in specialCharacters }

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar
    }

    fun isPasswordMatching(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }


    fun postCustomer(customer: CustomerRequest) {
        lifecycleScope.launch {
            authenticationViewModel.postCustomer(customer)
            authenticationViewModel.customerResponseDetailsStateFlow.collectLatest {
                when (it) {
                    is ApiState.Failure -> {
                        Log.d("postCustomer", "Failure: ${it.message}")
                    }

                    is ApiState.Loading -> {
                        Log.d(TAG, "postCustomer: Loading")
                    }

                    is ApiState.Success -> {
                        Log.d(TAG, "postCustomer: ya hlawa ya wlaaaaaaaaaaaaaaaaa")
                        Log.d(TAG, "postCustomer:${it.data.customer.id} ")
                        sharedPreferences.edit().putString(MyKey.MY_CUSTOMER_ID,"${it.data.customer.id}").apply()
                    }
                }
            }

        }
    }

}