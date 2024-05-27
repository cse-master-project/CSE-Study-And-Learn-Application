package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse_study_and_learn_application.databinding.FragmentMatingQuizBinding

/**
 * Mating quiz fragment
 *
 * @constructor Create empty Mating quiz fragment
 * @author JYH
 * @since 2024-03-28
 */
class MatingQuizFragment : Fragment() {

    lateinit var binding: FragmentMatingQuizBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMatingQuizBinding.inflate(inflater)
        return binding.root
    }

    companion object {
        fun newInstance(contents: String): MatingQuizFragment {
            val args = Bundle()

            val fragment = MatingQuizFragment()
            args.putString("contents", contents)
            fragment.arguments = args
            return fragment
        }
    }

}