package com.example.common.utils

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.common.R



/**
 * Update view visibility.
 */
@BindingAdapter("visibility")
fun View.setVisibility(visible: Boolean?) {
    visible?.apply {
        if (this) {
            this@setVisibility.visibility = View.VISIBLE
        } else {
            this@setVisibility.visibility = View.GONE
        }
    }
}

@BindingAdapter("bind:imageUrl")
fun loadImage(imageView: ImageView, url: String?) {
    if (url != "") {
        Glide.with(imageView.context)
            .load(url)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView)
    }
}

