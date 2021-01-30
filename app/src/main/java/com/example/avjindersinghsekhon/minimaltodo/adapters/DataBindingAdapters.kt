package com.example.avjindersinghsekhon.minimaltodo.adapters

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("isViewGone")
fun isViewGone(v : View, isGone : Boolean){
    v.visibility = if (isGone){
        View.GONE
    }else{
        View.VISIBLE
    }
}
