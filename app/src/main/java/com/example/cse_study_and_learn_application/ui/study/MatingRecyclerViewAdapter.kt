package com.example.cse_study_and_learn_application.ui.study

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.cse_study_and_learn_application.R

class MatingRecyclerViewAdapter(
    private val options: List<String>,
    private val isLeft: Boolean, // 왼쪽 또는 오른쪽을 구분하기 위한 플래그
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MatingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_btn_mating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = if (isLeft) {
            (position + 1).toString() // 왼쪽은 1, 2, 3, 4, 5 ...
        } else {
            ('A' + position).toChar().toString() // 오른쪽은 A, B, C, D ...
        }
        holder.button.text = text
        holder.button.setOnClickListener { v: View? ->
            listener.onItemClick(position, text)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, option: String?)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var button: Button = itemView.findViewById(R.id.btn)
    }
}
