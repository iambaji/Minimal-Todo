package com.example.avjindersinghsekhon.minimaltodo.Settings
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.CheckBoxPreference
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import android.preference.PreferenceFragment
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.utility.PreferenceKeys

class SettingsFragment : PreferenceFragment(), OnSharedPreferenceChangeListener {
    var app: AnalyticsApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_layout)
        app = activity.application as AnalyticsApplication
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preferenceKeys = PreferenceKeys(resources)
        if (key == preferenceKeys.night_mode_pref_key) {
            val themePreferences = activity.getSharedPreferences(THEME_PREFERENCES, Context.MODE_PRIVATE)
            val themeEditor = themePreferences.edit()
            //We tell our MainLayout to recreate itself because mode has changed
            themeEditor.putBoolean(RECREATE_ACTIVITY, true)
            val checkBoxPreference = findPreference(preferenceKeys.night_mode_pref_key) as CheckBoxPreference
            if (checkBoxPreference.isChecked) {
                //Comment out this line if not using Google Analytics
                app!!.send(this, "Settings", "Night Mode used")
                themeEditor.putString(THEME_SAVED, DARKTHEME)
            } else {
                themeEditor.putString(THEME_SAVED, LIGHTTHEME)
            }
            themeEditor.apply()
            activity.recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}