package com.asoft.blog.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.asoft.blog.R
import com.google.android.material.button.MaterialButton

fun View.showViewWithAnimation() {
    this.apply {
        alpha = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(null)
    }
}

fun View.scaleViewWithAnimation() {
    this.apply {
        alpha = 0f
        scaleY = 0f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .scaleY(1f)
            .setDuration(400)
            .setListener(null)
    }
}

fun View.translateViewWithAnimation() {
    this.apply {
        alpha = 0f
        translationY = -50f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .translationY(1f)
            .setDuration(400)
            .setListener(null)
    }
}

fun View.translateFromDownViewWithAnimation() {
    this.apply {
        alpha = 0f
        translationY = 250f
        visibility = View.VISIBLE
        animate()
            .alpha(1f)
            .translationY(1f)
            .setDuration(500)
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

fun AppCompatActivity.showAlertDialog(
    mTitle: String?,
    mSubtitle: String?,
    anim: Int,
    onActionPositive: (() -> Any?)?,
    onActionNegative: (() -> Any?)?,
    negativeButtonText: String?,
    positiveButtonText: String?
) {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
    dialog.setContentView(R.layout.dialog_loading)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    );
    dialog.setCancelable(true)
    dialog.setCanceledOnTouchOutside(true)

    val title = dialog.findViewById<TextView>(R.id.dialogTitle)
    val subtitle = dialog.findViewById<TextView>(R.id.dialogSubtitle)
    val btnPositive = dialog.findViewById<MaterialButton>(R.id.btnPositive)
    val btnNegative = dialog.findViewById<MaterialButton>(R.id.btnNegative)
    val lottieImage = dialog.findViewById<LottieAnimationView>(R.id.lottie)

    if (mTitle != null) {
        title.text = mTitle
    } else {
        title.hideViewWithAnimation()
    }

    if (mSubtitle != null) {
        subtitle.text = mSubtitle
    }else {
        subtitle.hideViewWithAnimation()
    }

    if (negativeButtonText != null) {
        btnNegative.text = negativeButtonText
        btnNegative.setOnClickListener {
            onActionNegative?.invoke()
            dialog.dismiss()
        }
    } else {
        btnNegative.hideViewWithAnimation()
    }

    if (positiveButtonText != null) {
        btnPositive.text = positiveButtonText
        btnPositive.setOnClickListener {
            onActionPositive?.invoke()
            dialog.dismiss()
        }
    } else {
        btnPositive.hideViewWithAnimation()
    }

    lottieImage.setAnimation(anim)
    lottieImage.loop(true)
    dialog.show()
}

fun Fragment.showAlertDialog(
    mTitle: String?,
    mSubtitle: String?,
    anim: Int,
    onActionPositive: (() -> Any?)?,
    onActionNegative: (() -> Any?)?,
    negativeButtonText: String?,
    positiveButtonText: String?
) {
    val dialog = Dialog(requireContext())
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
    dialog.setContentView(R.layout.dialog_loading)
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    );
    dialog.setCancelable(true)
    dialog.setCanceledOnTouchOutside(true)

    val title = dialog.findViewById<TextView>(R.id.dialogTitle)
    val subtitle = dialog.findViewById<TextView>(R.id.dialogSubtitle)
    val btnPositive = dialog.findViewById<MaterialButton>(R.id.btnPositive)
    val btnNegative = dialog.findViewById<MaterialButton>(R.id.btnNegative)
    val lottieImage = dialog.findViewById<LottieAnimationView>(R.id.lottie)

    if (mTitle != null) {
        title.text = mTitle
    } else {
        title.hideViewWithAnimation()
    }

    if (mSubtitle != null) {
        subtitle.text = mSubtitle
    }else {
        subtitle.hideViewWithAnimation()
    }

    if (negativeButtonText != null) {
        btnNegative.text = negativeButtonText
        btnNegative.setOnClickListener {
            onActionNegative?.invoke()
            dialog.dismiss()
        }
    } else {
        btnNegative.hideViewWithAnimation()
    }

    if (positiveButtonText != null) {
        btnPositive.text = positiveButtonText
        btnPositive.setOnClickListener {
            onActionPositive?.invoke()
            dialog.dismiss()
        }
    } else {
        btnPositive.hideViewWithAnimation()
    }

    lottieImage.setAnimation(anim)
    lottieImage.loop(true)
    dialog.show()
}