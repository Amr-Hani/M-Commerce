package com.example.mcommerce

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mcommerce.databinding.ActivityMain2Binding
import com.example.mcommerce.my_key.MyKey

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            this.getSharedPreferences(MyKey.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the Toolbar as the ActionBar
        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_category, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val guest = sharedPreferences.getString(MyKey.GUEST, "notguest")

        binding.imageView2.setOnClickListener {
            if (guest != "GUEST") {
                navController.navigate(R.id.favoriteFragment)
            } else {

                AlertDialog.Builder(this)
                    .setTitle("Regester")
                    .setMessage("if you want to see the favorite you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        binding.imageView.setOnClickListener {
            if (guest != "GUEST") {
                navController.navigate(R.id.cartFragment)
            } else {

                AlertDialog.Builder(this)
                    .setTitle("Regester")
                    .setMessage("if you want to see the cart you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
        binding.imageView.setOnClickListener {
            if (guest != "GUEST") {

                navController.navigate(R.id.cartFragment)
            } else {

                AlertDialog.Builder(this)
                    .setTitle("Regester")
                    .setMessage("if you want to see the cart you must register")
                    .setPositiveButton("Yes") { dialog, _ ->
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }


        binding.imageView3.setOnClickListener {
            navController.navigate(R.id.searchFragment)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
