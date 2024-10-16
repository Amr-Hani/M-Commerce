package com.example.mcommerce.ui.authentication.view

import android.content.Context
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
import androidx.navigation.Navigation
import com.example.mcommerce.R
import com.example.mcommerce.databinding.FragmentLogInBinding
import com.example.mcommerce.model.firebase.FireBaseDataSource
import com.example.mcommerce.model.firebase.Repo
import com.example.mcommerce.my_key.MyKey
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModel
import com.example.mcommerce.ui.authentication.viewmodel.AuthenticationViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {
    lateinit var binding: FragmentLogInBinding

    lateinit var sharedPreferences: SharedPreferences
    lateinit var authenticationViewModel: AuthenticationViewModel
    lateinit var authenticationViewModelFactory: AuthenticationViewModelFactory
    lateinit var mAuth: FirebaseAuth
    private val TAG = "LogInFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            )
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
        }
        else{
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

    override fun onStart() {
        super.onStart()
        val user = authenticationViewModel.checkIfEmailVerified()
        val action = LogInFragmentDirections.actionLogInFragmentToProductInfoFragment()
        Navigation.findNavController(binding.root).navigate(action)
    }

}