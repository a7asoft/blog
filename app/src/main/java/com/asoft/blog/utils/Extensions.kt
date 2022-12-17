package com.asoft.blog.utils

import android.view.View

fun View.showViewWithAnimation() {
    this.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(200)
            .setListener(null)
    }
}

fun View.hideViewWithAnimation() {
    this.apply {
        alpha = 1f
        visibility = View.GONE
        animate()
            .alpha(0f)
            .setDuration(200)
            .setListener(null)
    }
}