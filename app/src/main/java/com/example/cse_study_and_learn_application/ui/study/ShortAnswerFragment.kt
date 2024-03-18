package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding
import com.example.cse_study_and_learn_application.databinding.FragmentShortAnswerBinding

/**
 * Short answer fragment
 *
 * @constructor Create empty Short answer fragment
 * @author JYH
 * @since 2024-03-18
 */
class ShortAnswerFragment : Fragment() {

    private lateinit var binding: FragmentShortAnswerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentShortAnswerBinding.inflate(inflater)
        return binding.root
    }



}