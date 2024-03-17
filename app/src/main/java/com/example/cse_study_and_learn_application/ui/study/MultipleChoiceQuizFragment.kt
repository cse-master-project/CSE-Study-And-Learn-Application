package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding

/**
 * A simple [Fragment] subclass.
 * Use the [MultipleChoiceQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @author JYH
 * @since 2024-03-17
 */
class MultipleChoiceQuizFragment : Fragment() {

    private lateinit var binding: FragmentMultipleChoiceQuizBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMultipleChoiceQuizBinding.inflate(inflater)
        return binding.root
    }
}