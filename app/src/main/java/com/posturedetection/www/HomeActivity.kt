package com.posturedetection.www

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment_activity)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_detection,
                R.id.navigation_statistics,
                R.id.navigation_profile

            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.getOrCreateBadge(R.id.navigation_detection)?.number = 2












//        Toast.makeText(this , "Welcome" , Toast.LENGTH_SHORT).show()
//
//        auth = FirebaseAuth.getInstance()
//
//        val email = intent.getStringExtra("email")
//        val displayName = intent.getStringExtra("name")
//
//        findViewById<TextView>(R.id.textView).text = email + "\n" + displayName
//
//        findViewById<Button>(R.id.signOutBtn).setOnClickListener {
//            auth.signOut()
//            startActivity(Intent(this , MainActivity::class.java))
//        }
    }



}