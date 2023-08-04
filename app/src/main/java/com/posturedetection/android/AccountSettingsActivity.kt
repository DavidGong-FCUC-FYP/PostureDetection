package com.posturedetection.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.posturedetection.android.widget.TitleLayout
import com.posturedetection.android.databinding.ActivityAccountSettingsBinding

class AccountSettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountSettingsBinding

    private lateinit var titleLayout: TitleLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titleLayout = binding.tlTitle
        titleLayout.setTextView_title("Account Settings")
    }
}