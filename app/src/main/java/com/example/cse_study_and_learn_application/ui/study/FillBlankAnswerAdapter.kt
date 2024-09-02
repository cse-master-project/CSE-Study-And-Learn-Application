package com.example.cse_study_and_learn_application.ui.study

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cse_study_and_learn_application.databinding.ItemFillBlankAnswerBinding

class FillBlankAnswerAdapter(private val answers: MutableList<String>) :
    RecyclerView.Adapter<FillBlankAnswerAdapter.AnswerViewHolder>() {

    inner class AnswerViewHolder(private val binding: ItemFillBlankAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.etAnswer.hint = "${position + 1} 번 답.."

            binding.etAnswer.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    answers[position] = s.toString()
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        fun disableInput() {
            binding.etAnswer.isEnabled = false
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemFillBlankAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == "disable") {
            holder.disableInput()
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount() = answers.size

    fun getAnswers(): List<String> = answers

    fun disableAllInputs() {
        answers.indices.forEach { position ->
            notifyItemChanged(position, "disable")
        }
    }
}