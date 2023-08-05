package com.posturedetection.android.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.google.gson.Gson
import com.posturedetection.android.data.model.AccountSettings

class ChangeLanguageUtil {
    fun changeLanguage(account_settings_json: String) {
        if (account_settings_json != null){
            var gson = Gson()
            var accountSettings = gson.fromJson(account_settings_json, AccountSettings::class.java)
            if (accountSettings != null){
                var language = "en"
                when(accountSettings.language){
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
            }
        }
    }
}