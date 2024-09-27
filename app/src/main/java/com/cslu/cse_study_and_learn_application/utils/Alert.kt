package com.cslu.cse_study_and_learn_application.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun showAlert(
    context: Context,
    title: String,
    message: String,
    positiveButtonText: String = "확인",
    negativeButtonText: String? = null,
    onPositiveClick: (() -> Unit)? = null,
    onNegativeClick: (() -> Unit)? = null
) {
    val builder = MaterialAlertDialogBuilder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButtonText) { dialog, _ ->
            onPositiveClick?.invoke()
            dialog.dismiss()
        }

    if (negativeButtonText != null) {
        builder.setNegativeButton(negativeButtonText) { dialog, _ ->
            onNegativeClick?.invoke()
            dialog.dismiss()
        }
    }

    builder.show()
}