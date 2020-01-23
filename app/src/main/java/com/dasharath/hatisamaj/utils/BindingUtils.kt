package com.dasharath.hatisamaj.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dasharath.hatisamaj.R


@BindingAdapter("android:src")
fun setImageViewResource(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.ic_person_male).into(imageView)
}

fun setPostImage(imageView: ImageView, url: String) {
    Glide.with(imageView.context).load(url).placeholder(R.drawable.placeholder).into(imageView)
}