package com.posturedetection.www

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)




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