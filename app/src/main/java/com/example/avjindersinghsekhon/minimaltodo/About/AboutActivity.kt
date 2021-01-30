package com.example.avjindersinghsekhon.minimaltodo.About

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NavUtils
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.Analytics.AnalyticsApplication
import com.example.avjindersinghsekhon.minimaltodo.AppDefault.AppDefaultActivity
import com.example.avjindersinghsekhon.minimaltodo.R
import com.example.avjindersinghsekhon.minimaltodo.utility.*

class AboutActivity : AppDefaultActivity() {
    private val mVersionTextView: TextView? = null
    private var appVersion = "0.1"
    private var toolbar: Toolbar? = null
    private val contactMe: TextView? = null
    var theme: String? = null

    //    private UUID mId;
    private val app: AnalyticsApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        theme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME)
        if (theme == DARKTHEME) {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_DarkTheme)
        } else {
            Log.d("OskarSchindler", "One")
            setTheme(R.style.CustomStyle_LightTheme)
        }
        super.onCreate(savedInstanceState)
        //        mId = (UUID)i.getSerializableExtra(TodoNotificationService.TODOUUID);
        val backArrow = resources.getDrawable(R.drawable.abc_ic_ab_back_material)
        backArrow?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            appVersion = info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(backArrow)
        }
    }

    override fun contentViewLayoutRes(): Int {
        return R.layout.about_layout
    }

    override fun createInitialFragment(): Fragment {
        return AboutFragment.newInstance()
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