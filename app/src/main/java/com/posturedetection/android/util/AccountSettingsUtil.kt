package com.posturedetection.android.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.gson.Gson
import com.posturedetection.android.data.model.AccountSettings

class AccountSettingsUtil {
    companion object {
        fun init(accountSettings: AccountSettings) {
            if (accountSettings != null) {
                var language = "en"
                when (accountSettings.language) {
                    0 -> {
                        language = "en"
                    }

                    1 -> {
                        language = "zh"
                    }

                    2 -> {
                        language = "ms"
                    }
                }
                val locales = LocaleListCompat.forLanguageTags(language)
                AppCompatDelegate.setApplicationLocales(locales)

                when (accountSettings.themeAppearance) {
                    0 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }

                    1 -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }
    }
}