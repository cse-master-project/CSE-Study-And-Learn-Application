package com.example.cse_study_and_learn_application.ui.study

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.ActivityQuizBinding

/**
 * Quiz activity
 *
 * @constructor Create empty Quiz activity
 * @author JYH
 * @since 2024-03-22
 */
class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding;

    private val fragmentManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val multipleChoiceQuizFragment = MultipleChoiceQuizFragment()
        val shortAnswerQuizFragment = ShortAnswerQuizFragment()
        val trueFalseQuizFragment = TrueFalseQuizFragment()
        val matingQuizFragment = MatingQuizFragment()
        val fillBlankQuizFragment = FillBlankQuizFragment()

        fragmentManager.beginTransaction()
            .add(binding.fragmentContainerView.id, fillBlankQuizFragment)
            .addToBackStack(null)
            .commit()
    }
}