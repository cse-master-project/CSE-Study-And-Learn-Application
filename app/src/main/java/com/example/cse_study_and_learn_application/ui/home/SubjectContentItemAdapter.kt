package com.example.cse_study_and_learn_application.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.cse_study_and_learn_application.databinding.ItemSubjectCatBinding
import com.example.cse_study_and_learn_application.databinding.ItemSubjectContentBinding
import com.example.cse_study_and_learn_application.model.SubjectContent

class SubjectContentItemAdapter(private val contents: List<SubjectContent>) :
    RecyclerView.Adapter<SubjectContentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding = ItemSubjectContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SubjectContentViewHolder(binding)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val content = contents[position]
        holder.bind(content)
    }
}


class SubjectContentViewHolder(private val binding: ItemSubjectContentBinding) : ViewHolder(binding.root){
    fun bind(content: SubjectContent) {
        binding.tvContentTitle.text = content.title
    }

}