package com.example.avjindersinghsekhon.minimaltodo.Settings

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import com.example.avjindersinghsekhon.minimaltodo.utility.*
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.R

class SettingsActivity : AppCompatActivity() {
    var app: AnalyticsApplication? = null
    override fun onResume() {
        super.onResume()
        app!!.send(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        app = application as AnalyticsApplication
        val theme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        if (theme == LIGHTTHEME) {
            setTheme(R.style.CustomStyle_LightTheme)
        } else {
            setTheme(R.style.CustomStyle_DarkTheme)
        }
        super.onCreate(savedInstanceState)
        //        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }
        val fm = fragmentManager
        fm.beginTransaction().replace(R.id.mycontent, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (NavUtils.getParentActivityName(this) != null) {
                    NavUtils.navigateUpFromSameTask(this)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}