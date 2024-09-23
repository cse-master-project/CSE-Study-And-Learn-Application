package com.example.cse_study_and_learn_application.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cse_study_and_learn_application.R

class MatingOptionAdapter(
    private val descriptions: List<String>,  // 설명 텍스트 리스트
    private val isLeft: Boolean  // 왼쪽이면 true, 오른쪽이면 false
) : RecyclerView.Adapter<MatingOptionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val circleText: TextView = view.findViewById(R.id.circle_text)
        val descriptionText: TextView = view.findViewById(R.id.description_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mating_number, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // 왼쪽이면 숫자, 오른쪽이면 알파벳 증가
        if (isLeft) {
            // 숫자가 1부터 증가
            holder.circleText.text = (position + 1).toString()
        } else {
            // 알파벳이 A부터 증가
            holder.circleText.text = ('A' + position).toString()
        }

        // 설명 텍스트를 넣음
        holder.descriptionText.text = descriptions[position]
    }

    override fun getItemCount(): Int {
        return descriptions.size
    }
}
