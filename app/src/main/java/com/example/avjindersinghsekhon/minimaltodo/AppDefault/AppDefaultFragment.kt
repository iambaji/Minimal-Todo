package com.example.avjindersinghsekhon.minimaltodo.AppDefault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class AppDefaultFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutRes(), container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @LayoutRes
    protected abstract fun layoutRes(): Int
}