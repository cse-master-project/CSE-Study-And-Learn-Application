package com.example.cse_study_and_learn_application.ui.other

import android.content.Context
import android.graphics.Gainmap
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.utils.dpToPx

/**
 * Utility object for creating custom design toasts.
 */
object DesignToast {
    /**
     * Enum class representing different layout designs for toasts.
     *
     * @property layoutId The resource ID of the layout.
     */
    enum class LayoutDesign(val layoutId: Int) {
        SUCCESS(R.layout.toast_success),
        ERROR(R.layout.toast_error),
        INFO(R.layout.toast_info)
    }

    /**
     * Creates a custom toast with the specified design, title, and message.
     *
     * @param context The context to use. Usually your [android.app.Application] or [android.app.Activity] object.
     * @param design The design of the toast, as specified in [LayoutDesign].
     * @param message The message text to display in the toast.
     * @param title The title text to display in the toast.
     * @param duration How long to display the message. Either [Toast.LENGTH_SHORT] or [Toast.LENGTH_LONG]. Default is [Toast.LENGTH_SHORT].
     * @return The created [Toast] object.
     */
    fun makeText(context: Context, design: LayoutDesign, message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(design.layoutId, null)

        val toastMessage: TextView = layout.findViewById(R.id.tv_toast_message)

        toastMessage.text = message

        return Toast(context).apply {
            this.duration = duration
            view = layout
            setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        }
    }

    fun makeTextWithTitle(context: Context, design: LayoutDesign, title: String, message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(design.layoutId, null)

        val toastTitle: TextView = layout.findViewById(R.id.tv_toast_title)
        val toastMessage: TextView = layout.findViewById(R.id.tv_toast_message)

        toastTitle.text = title
        toastMessage.text = message

        return Toast(context).apply {
            this.duration = duration
            view = layout
            setGravity(Gravity.FILL_HORIZONTAL or Gravity.BOTTOM, 0, 0)
        }
    }
}
