package com.example.cse_study_and_learn_application.ui.study

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.connector.ConnectorRepository
import com.example.cse_study_and_learn_application.databinding.FragmentBottomSheetGradingBinding
import com.example.cse_study_and_learn_application.ui.login.AccountAssistant
import com.example.cse_study_and_learn_application.utils.QuizType
import com.example.cse_study_and_learn_application.utils.getQuizTypeFromInt
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [BottomSheetGradingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BottomSheetGradingFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetGradingBinding
    private var onNextQuizListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetGradingBinding.inflate(inflater)

        requireArguments().let {
            val quizId = it.getInt("quizId")
            val userAnswer = it.getString("userAnswer").toString()
            val answer = it.getString("answer").toString()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet = bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        val behavior = BottomSheetBehavior.from(bottomSheet!!)
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = getBottomSheetDialogDefaultHeight()
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getBottomSheetDialogDefaultHeight(): Int {
        return getWindowHeight() * 1 / 2
    }

    private fun getWindowHeight(): Int {
        val windowManager = ContextCompat.getSystemService(requireContext(), WindowManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager?.currentWindowMetrics
            val insets = metrics?.windowInsets?.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            metrics?.bounds?.height()?.minus(insets?.top ?: 0)?.minus(insets?.bottom ?: 0) ?: 0
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager?.defaultDisplay?.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    fun setOnNextQuizListener(listener: () -> Unit) {
        onNextQuizListener = listener
    }

    companion object {
        fun newInstance(
            isCorrect: Boolean,
            commentary: String,
        ): BottomSheetGradingFragment {
            return BottomSheetGradingFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("isCorrect", isCorrect)
                    putString("commentary", commentary)
                }
            }
        }
    }
}