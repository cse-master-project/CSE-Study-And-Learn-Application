package com.example.cse_study_and_learn_application.ui.other

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.cse_study_and_learn_application.R

/**
 * Dialog quest message
 *
 * @property questText
 * @property positiveText
 * @property negativeText
 * @constructor
 *
 * @param context
 *
 * @since 2024-03-17
 * @author KJY
 */
class DialogQuestMessage(context: Context,
                         private val questText: String = "질문",
                         private val positiveText: String = "예",
                         private val negativeText: String = "아니요") : Dialog(context) {

    private lateinit var positiveAction: (() -> Unit)
    private lateinit var negativeAction: (() -> Unit)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quest_message, null)
        setContentView(dialogView)

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val question = dialogView.findViewById<TextView>(R.id.tv_quest)
        val positive = dialogView.findViewById<TextView>(R.id.tv_positive)
        val negative = dialogView.findViewById<TextView>(R.id.tv_negative)
        val cardView = dialogView.findViewById<CardView>(R.id.cv_root)

        // 텍스트 설정
        question.text = questText
        positive.text = positiveText
        negative.text = negativeText


        // 카드뷰 마진 설정
        val cardMarginInDp = 20f
        val cardMarginInPixels = (cardMarginInDp * context.resources.displayMetrics.density).toInt()
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(cardMarginInPixels, cardMarginInPixels, cardMarginInPixels, cardMarginInPixels)
        cardView.layoutParams = layoutParams

        // 리스너 설정
        positive.setOnClickListener {
            positiveAction()
        }

        negative.setOnClickListener {
            negativeAction()
        }
    }

    fun setPositive(action: () -> Unit) {
        positiveAction = action
    }

    fun setNegative(action: () -> Unit) {
        negativeAction = action
    }
}