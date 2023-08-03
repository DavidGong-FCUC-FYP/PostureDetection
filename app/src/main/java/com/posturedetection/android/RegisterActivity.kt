package com.posturedetection.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.posturedetection.android.data.model.User
import com.posturedetection.android.util.ActivityCollector
import com.posturedetection.android.util.MD5
import com.posturedetection.android.util.PhotoUtils
import org.litepal.LitePal
import org.litepal.tablemanager.Connector
import com.posturedetection.android.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityRegisterBinding


    private lateinit var etAccountEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityCollector.addActivity(this)

        etAccountEmail =binding.etAccountEmail
        etPassword = binding.etPassword
        registerButton = binding.register
        Connector.getDatabase()

        // Register logic implementation
        registerButton.setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCollector.removeActivity(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.register -> {
                val email = etAccountEmail.text.toString()
                var password = etPassword.text.toString()

                password = MD5.md5(password) // MD5 encryption
                val users = LitePal.where("email==?", email).find(User::class.java)
                val mToast = Toast.makeText(this, null, Toast.LENGTH_SHORT)

                // Check if the username already exists
                if (!users.isEmpty()) {
                    mToast.setText(R.string.user_already_exist)
                    mToast.show()
                } else {
                    // If the username does not exist, create a new user
                    val user = User()
                    user.email = email
                    user.password = password
                    val name = email.split("@".toRegex()).toTypedArray()[0]
                    user.name = name

                    // Default settings: not remember password and set default portrait
                    user.portrait = PhotoUtils().file2byte(this, "profile.png")
                    user.remember = 0
                    user.save()

                    mToast.setText(R.string.register_success)
                    mToast.show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}
