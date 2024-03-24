package com.example.cse_study_and_learn_application.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cse_study_and_learn_application.R
import com.example.cse_study_and_learn_application.databinding.ItemSubjectCatBinding
import com.example.cse_study_and_learn_application.databinding.ItemSubjectContentBinding
import com.example.cse_study_and_learn_application.model.SubjectContent
import com.example.cse_study_and_learn_application.utils.setTextColorAsLinearGradient

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
class SubjectContentItemAdapter(private val contents: List<SubjectContent>, private val context: Context) :
    RecyclerView.Adapter<SubjectContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding = ItemSubjectContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SubjectContentViewHolder(binding, context)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val content = contents[position]
        holder.bind(content)
    }
}


class SubjectContentViewHolder(private val binding: ItemSubjectContentBinding, private val context: Context) : ViewHolder(binding.root){
    fun bind(content: SubjectContent) {
        binding.tvContentTitle.text = content.title


    }

}