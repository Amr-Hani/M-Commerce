package com.example.mcommerce

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mcommerce.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mAuth: FirebaseAuth
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.nav_fragment)
//        mAuth = FirebaseAuth.getInstance()
//        val email = "3laaeisa@gmail.com"
//        val password = "Civil5750730"
//        if (isEmailValid(email)) {
//            checkIfEmailExists(email, password)
//        } else {
//            Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
//        }
//        signInWithEmailAndPassword(email,password)
    }



    private fun checkIfEmailVerified() {
        val user = mAuth.currentUser
        if (user != null) {
            if (user.isEmailVerified) {
                // البريد الإلكتروني تم التحقق منه
                Toast.makeText(this, "Email is verified.", Toast.LENGTH_SHORT).show()

                // قم بإجراء العملية التي ترغب بها هنا
            } else {
                // البريد الإلكتروني لم يتم التحقق منه
                Toast.makeText(this, "Email is not verified. Please verify your email.", Toast.LENGTH_SHORT).show()
                // يمكنك أيضًا إرسال بريد إلكتروني للتحقق مرة أخرى إذا لزم الأمر
            }
        } else {
            // المستخدم ليس مسجلاً دخول
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }

     //دالة للتسجيل بالبريد الإلكتروني وكلمة المرور
    private fun signUp(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign Up Successful.", Toast.LENGTH_SHORT).show()
                    sendVerificationEmail(mAuth.currentUser)
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: " + task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
    private fun sendVerificationEmail(user: FirebaseUser?) {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Verification email sent to " + user.email, Toast.LENGTH_SHORT).show()
            } else {
                Log.e("TAG", "sendEmailVerification", task.exception)
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }


private fun signInWithEmailAndPassword(email: String, password: String) {
    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
            // FirebaseUser user = mAuth.getCurrentUser();
            Toast.makeText(this, "Authentication success.", Toast.LENGTH_SHORT).show()
            checkIfEmailVerified()
        } else {
            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
            val exceptionMessage = task.exception?.message ?: "Authentication failed."
            Log.d("TAG", "signInWithEmailAndPassword: $exceptionMessage")
        }
    }
}

}
































//}


