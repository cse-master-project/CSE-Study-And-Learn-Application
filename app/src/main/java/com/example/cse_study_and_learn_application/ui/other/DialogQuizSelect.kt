package com.example.cse_study_and_learn_application.ui.other

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.ui.home.HomeViewModel
import com.google.android.flexbox.FlexboxLayout

class DialogQuizSelect(private val context: Context) : Dialog(context) {

    private lateinit var positiveAction: (() -> Unit)
    private lateinit var negativeAction: (() -> Unit)
    private var cardMarginInDp = 10f
    private lateinit var spinner: Spinner
    private lateinit var flexboxLayout: FlexboxLayout
    private lateinit var homeViewModel: HomeViewModel
    lateinit var swDefaultQuiz: SwitchCompat
    lateinit var swUserQuiz: SwitchCompat
    lateinit var swSolvedQuiz: SwitchCompat

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quiz_select, null)
        setContentView(dialogView)

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        homeViewModel = ViewModelProvider(context as AppCompatActivity)[HomeViewModel::class.java]

        val positive = dialogView.findViewById<TextView>(R.id.tv_positive)
        val negative = dialogView.findViewById<TextView>(R.id.tv_negative)
        val add = dialogView.findViewById<TextView>(R.id.tv_add)
        val cardView = dialogView.findViewById<CardView>(R.id.cv_root)
        spinner = dialogView.findViewById(R.id.spinner_quiz)
        swDefaultQuiz = dialogView.findViewById(R.id.sw_default_quiz)
        swUserQuiz = dialogView.findViewById(R.id.sw_user_quiz)
        swSolvedQuiz = dialogView.findViewById(R.id.sw_solved_quiz)
        flexboxLayout = dialogView.findViewById(R.id.flexbox_selected_quiz)

        val cardMarginInPixels = (cardMarginInDp * context.resources.displayMetrics.density).toInt()
        val layoutParams = cardView.layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.setMargins(cardMarginInPixels, cardMarginInPixels, cardMarginInPixels, cardMarginInPixels)
        cardView.layoutParams = layoutParams

        positive.setOnClickListener {
            positiveAction()
        }

        negative.setOnClickListener {
            negativeAction()
        }

        add.setOnClickListener {
            val selectedSubject = spinner.selectedItem.toString()
            if (homeViewModel.flexboxSelectedSubjects.value?.contains(selectedSubject) == true) {
                Toast.makeText(context, "이미 추가된 항목입니다.", Toast.LENGTH_SHORT).show()
            } else {
                addSubjectToFlexbox(selectedSubject)
            }
        }

        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.9).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        homeViewModel.flexboxSelectedSubjects.observe(context as AppCompatActivity) { subjects ->
            updateFlexbox(subjects)
        }
    }

    fun getSpinner(): Spinner {
        return spinner
    }

    fun setPositive(action: () -> Unit) {
        positiveAction = action
    }

    fun setNegative(action: () -> Unit) {
        negativeAction = action
    }

    private fun addSubjectToFlexbox(subject: String) {
        homeViewModel.flexboxSelectedSubjects.value?.let {
            it.add(subject)
            homeViewModel.flexboxSelectedSubjects.value = it
        }
    }

    private fun updateFlexbox(subjects: List<String>) {
        flexboxLayout.removeAllViews()
        val inflater = LayoutInflater.from(context)
        for (subject in subjects) {
            val itemView = inflater.inflate(R.layout.item_flexbox_subject, flexboxLayout, false)
            val tvName = itemView.findViewById<TextView>(R.id.tv_name)
            val tvRemove = itemView.findViewById<TextView>(R.id.tv_remove)

            tvName.text = subject

            tvRemove.setOnClickListener {
                homeViewModel.flexboxSelectedSubjects.value?.let {
                    it.remove(subject)
                    homeViewModel.flexboxSelectedSubjects.value = it
                }
            }

            flexboxLayout.addView(itemView)
        }
    }
}
