package com.example.avjindersinghsekhon.minimaltodo.AppDefault

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.avjindersinghsekhon.minimaltodo.R

abstract class AppDefaultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentViewLayoutRes())
        setUpInitialFragment(savedInstanceState)
    }

    private fun setUpInitialFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, createInitialFragment())
                    .commit()
        }
    }

    @LayoutRes
    protected abstract fun contentViewLayoutRes(): Int
    protected abstract fun createInitialFragment(): Fragment
}