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
import com.example.mcommerce.model.pojos.CustomerRequest
import com.example.mcommerce.model.pojos.PostedCustomer
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModel
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {
    lateinit var binding: FragmentLogInBinding

    lateinit var sharedPreferences: SharedPreferences
    lateinit var authenticationViewModel: AuthenticationViewModel
    lateinit var authenticationViewModelFactory: AuthenticationViewModelFactory
    lateinit var mAuth: FirebaseAuth
    private val TAG = "LogInFragment"

    val address = Address(
        address1 = "3 Bank St",
        city = "Zagazig",
        province = "CA",
        phone = "+01008313390",
        zip = "12345",
        last_name = "Amr",
        first_name = "Hani",
        country = "Egypt"
    )

    val customer = PostedCustomer(
        first_name = "Amr",
        last_name = "Hani",
        email = "3mrhani@gmail.com",
        phone = "010083130390",
        verified_email = true,
        addresses = listOf(address),
        password = "Am#123456",
        password_confirmation = "Am#123456",
        send_email_welcome = false
    )

    val customerRequest = CustomerRequest(customer)

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
            sharedPreferences.edit().putString(MyKey.GUEST, "Guest")
                .apply()
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
        val email = binding.etUserNameLogin.text.toString()
        val password = binding.etPasswordLogin.text.toString()
        if (!email.isNullOrEmpty() || !password.isNullOrBlank()) {
            authenticationViewModel.logIn(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        customerRequest.postedCustomer.email = email
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
                sharedPreferences.edit().putString(MyKey.GUEST, "LogIn")
                    .apply()
                postCustomer(customerRequest)
                // هنا نافيجيت ي عم منير ع ال home screen اول متعمل login
//
//                val action = LogInFragmentDirections.actionLogInFragmentToProductInfoFragment()
//                Navigation.findNavController(binding.root).navigate(action)

//                val intent = Intent(activity, MainActivity2::class.java)
////                intent.putExtra("dataKey", "someData")// Optional: Passing data
//                startActivity(intent)

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
                        Log.d(TAG, "postCustomer:${it.data.customers.get(0).id} ")
                    }
                }
            }

        }
    }

}