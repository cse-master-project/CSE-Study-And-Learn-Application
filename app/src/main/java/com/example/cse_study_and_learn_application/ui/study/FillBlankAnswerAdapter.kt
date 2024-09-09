package com.example.cse_study_and_learn_application.ui.study

import android.annotation.SuppressLint
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
            binding.etAnswer.setText("") // 초기에 빈 텍스트로 설정

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

        fun setBackgroundColor(color: Int) {
            binding.etAnswer.setBackgroundColor(color)
        }

        fun setAnswerText(text: String) {
            binding.etAnswer.setText(text)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun resetAnswers() {
        answers.replaceAll { "" }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val binding = ItemFillBlankAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnswerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            when (payloads[0]) {
                is Int -> holder.setBackgroundColor(payloads[0] as Int)
                is String -> {
                    when (payloads[0]) {
                        "disable" -> holder.disableInput()
                        else -> holder.setAnswerText(payloads[0] as String)
                    }
                }
            }
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

    fun setItemBackgroundColor(position: Int, color: Int) {
        notifyItemChanged(position, color)
    }

    fun setItemText(position: Int, text: String) {
        answers[position] = text
        notifyItemChanged(position, text)
    }
}