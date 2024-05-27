package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.FragmentTrueFalseQuizBinding


/**
 * True false quiz fragment
 *
 * @constructor Create empty True false quiz fragment
 * @author JYH
 * @since 2024-03-18
 */
class TrueFalseQuizFragment : Fragment() {

    lateinit var binding: FragmentTrueFalseQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrueFalseQuizBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        fun newInstance(contents: String): TrueFalseQuizFragment {
            val args = Bundle()

            val fragment = TrueFalseQuizFragment()
            args.putString("contents", contents)
            fragment.arguments = args
            return fragment
        }
    }
}