package com.cslu.cse_study_and_learn_application.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cslu.cse_study_and_learn_application.R
import com.cslu.cse_study_and_learn_application.databinding.ItemSubjectContentBinding
import com.cslu.cse_study_and_learn_application.model.QuizContentCategory

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
    interface ToggleCheckBoxListener {
        fun clickToggle(title: String, selected: Boolean)
    }

    interface ItemClickListener {
        fun click(title: String, selected: Boolean)
    }

    var toggleCheckBox = false
    private var checkAll = true

    private var toggleCheckBoxListener: ToggleCheckBoxListener? = null
    private var itemClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectContentViewHolder {
        val binding = ItemSubjectContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectContentViewHolder(binding, context)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: SubjectContentViewHolder, position: Int) {
        val content = contents[position]
        holder.bind(content, toggleCheckBox, toggleCheckBoxListener, position, checkAll, itemClickListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun toggleCheckBoxVisibility(): Boolean {
        toggleCheckBox = !toggleCheckBox
        if (toggleCheckBox) {
            checkAll = toggleCheckBox
        }
        notifyDataSetChanged()
        return toggleCheckBox
    }

    fun getSelectedItems(): List<QuizContentCategory> {
        return contents.filter { it.selected }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun changeChapters(currentChapters: List<QuizContentCategory>) {
        contents = currentChapters
        // Log.d("test", "어댑터: $contents")
        notifyDataSetChanged()
    }

    fun setToggleCheckBoxListener(toggleCheckBoxListener: ToggleCheckBoxListener) {
        this.toggleCheckBoxListener = toggleCheckBoxListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setChangeCheck(flag: Boolean) {
        checkAll = flag
        notifyDataSetChanged()
    }

    fun setItemClickListener(listener: ItemClickListener) {
        this.itemClickListener = listener
    }

}

class SubjectContentViewHolder(
    private val binding: ItemSubjectContentBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        content: QuizContentCategory,
        toggleCheckBox: Boolean,
        toggleCheckBoxListener: SubjectContentItemAdapter.ToggleCheckBoxListener?,
        position: Int,
        checkAll: Boolean,
        itemClickListener: SubjectContentItemAdapter.ItemClickListener?
    ) {
        binding.tvContentTitle.text = content.title
        binding.tvCount.text = String.format("No. %d", position)

        if (toggleCheckBox) {
            binding.cbQuizSel.isChecked = true
            binding.cbQuizSel.isEnabled = false
            content.selected = true
            binding.cbQuizSel.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.light_gray_c5)
        } else {
            binding.cbQuizSel.isChecked = checkAll
            binding.cbQuizSel.isEnabled = true
            content.selected = checkAll
            binding.cbQuizSel.buttonTintList =
                ContextCompat.getColorStateList(context, R.color.light_blue_300)
        }

        binding.cbQuizSel.setOnClickListener {
            content.selected = binding.cbQuizSel.isChecked
            toggleCheckBoxListener?.clickToggle(content.title, content.selected)
        }

        binding.clRoot.setOnClickListener {
            content.selected = !binding.cbQuizSel.isChecked
            binding.cbQuizSel.isChecked = content.selected
            itemClickListener?.click(content.title, content.selected)
        }
    }
}
