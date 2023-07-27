package com.posturedetection.com

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = getSupportActionBar();

        loadFragment(DetectionFragment.newInstance())
        val bottom_navigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottom_navigation.setOnItemSelectedListener { item ->
            var fragment: Fragment
            when (item.itemId) {
                R.id.nav_detection -> {
                    toolbar!!.title = "Detection"
                    loadFragment(DetectionFragment.newInstance())
                    return@setOnItemSelectedListener true
                }
                R.id.nav_statistics -> {
                    toolbar!!.title = "Statistics"
                    loadFragment(ProfileFragment.newInstance("Statistics", "Statistics"))
                    return@setOnItemSelectedListener true
                }
                R.id.nav_profile -> {
                    toolbar!!.title = "Profile"
                    loadFragment(ProfileFragment.newInstance("Profile", "Profile"))
                    return@setOnItemSelectedListener true
                }
            }
            false
        }




    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }



}