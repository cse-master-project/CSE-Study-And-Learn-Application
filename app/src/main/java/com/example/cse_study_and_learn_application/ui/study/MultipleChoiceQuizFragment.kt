package com.example.cse_study_and_learn_application.ui.study

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cse_study_and_learn_application.databinding.FragmentMultipleChoiceQuizBinding
import com.google.android.material.card.MaterialCardView

/**
 *
 * Use the [MultipleChoiceQuizFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 * @constructor A simple [Fragment] subclass.
 * @author JYH
 * @since 2024-03-17
 */
class MultipleChoiceQuizFragment : Fragment() {

    private lateinit var binding: FragmentMultipleChoiceQuizBinding
    private val cards: ArrayList<MaterialCardView> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMultipleChoiceQuizBinding.inflate(inflater)


        cards.add(binding.cvAnswer1)
        cards.add(binding.cvAnswer2)
        cards.add(binding.cvAnswer3)
        cards.add(binding.cvAnswer4)

        cards[0].setOnClickListener {
            onlyOneCardViewToggle(cards[0])
        }
        cards[1].setOnClickListener {
            onlyOneCardViewToggle(cards[1])
        }
        cards[2].setOnClickListener {
            onlyOneCardViewToggle(cards[2])
        }
        cards[3].setOnClickListener {
            onlyOneCardViewToggle(cards[3])
        }

        return binding.root
    }

    private fun onlyOneCardViewToggle(cardView:MaterialCardView) {
        for(i in cards) {
            if (i.isChecked) {
                i.toggle()
            }
        }
        cardView.toggle()
    }
}