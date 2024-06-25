package com.example.cse_study_and_learn_application.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.ItemSubjectContentBinding
import com.example.cse_study_and_learn_application.model.QuizContentCategory

/**
 * Subject content item adapter
 *
 * @property contents
 * @property context
 * @constructor Create empty Subject content item adapter
 *
 * @author kjy
 * @since 2024-03-16
 *
 * 최근 주요 변경점
 * - 매개변수 context 추후 그라데이션 넣을지 고민 중
 * - 카드뷰 두 개로 구현했던 뷰를 제거하고 그냥 테두리 bg 넣음
 */
class SubjectContentItemAdapter(private var contents: List<QuizContentCategory>, private val context: Context) :
    RecyclerView.Adapter<SubjectContentViewHolder>() {
    private var toggleCheckBox = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding = ItemSubjectContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectContentViewHolder(binding, context)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val content = contents[position]
        holder.bind(content, toggleCheckBox)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun toggleCheckBoxVisibility() {
        toggleCheckBox = !toggleCheckBox
        notifyDataSetChanged()
    }

    fun getSelectedItems(): List<QuizContentCategory> {
        return contents.filter { it.selected }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeDetailSubjects(currentDetailSubjects: List<QuizContentCategory>) {
        contents = currentDetailSubjects
        Log.d("test", "어댑터: $contents")
        notifyDataSetChanged()
    }
}

class SubjectContentViewHolder(private val binding: ItemSubjectContentBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
    fun bind(content: QuizContentCategory, toggleCheckBox: Boolean) {
        binding.tvContentTitle.text = content.title
        if (toggleCheckBox) {
            binding.cbQuizSel.isChecked = true
            binding.cbQuizSel.isEnabled = false
            content.selected = true
            binding.cbQuizSel.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.light_gray_c5)
        } else {
            binding.cbQuizSel.isChecked = false
            binding.cbQuizSel.isEnabled = true
            content.selected = false
            binding.cbQuizSel.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.light_blue_300)
        }

        binding.cbQuizSel.setOnClickListener {
            content.selected = binding.cbQuizSel.isChecked
        }
    }
}
