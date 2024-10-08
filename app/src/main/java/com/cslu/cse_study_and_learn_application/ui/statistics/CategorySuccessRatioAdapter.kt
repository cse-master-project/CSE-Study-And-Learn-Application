package com.cslu.cse_study_and_learn_application.ui.statistics

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cslu.cse_study_and_learn_application.databinding.ItemEachCategoryRatioBinding
import com.cslu.cse_study_and_learn_application.model.CategorySuccessRatio

/**
 * Category success ratio adapter
 *
 * @property contents
 * @constructor Create empty Category success ratio adapter
 *
 * @since 2024-03-24
 * @author kjy
 */
class CategorySuccessRatioAdapter(private var contents: List<CategorySuccessRatio>, private val context: Context):
    RecyclerView.Adapter<CategorySuccessRatioAdapter.RatioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatioViewHolder {
        val binding =
            ItemEachCategoryRatioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatioViewHolder(binding, context)
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: RatioViewHolder, position: Int) {
        holder.bind(contents[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun itemUpdate(contents: List<CategorySuccessRatio>) {
        this.contents = contents
        notifyDataSetChanged()
    }

    class RatioViewHolder(
        private val binding: ItemEachCategoryRatioBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: CategorySuccessRatio) {
            binding.tvCategoryTitle.text = content.title
            val ratio = content.ratio
            val formattedRatio = when {
                ratio == 0.0 -> "0%"
                ratio % 1 == 0.0 -> String.format("%.0f%%", ratio)
                else -> String.format("%.1f%%", ratio)
            }
            binding.tvCategoryRatio.text = formattedRatio

//            binding.tvCategoryTitle.setTextColorAsLinearGradient(arrayOf(
//                ContextCompat.getColor(context, R.color.light_blue_400),
//                ContextCompat.getColor(context, R.color.light_blue_300)
//            ))
//
//            binding.tvCategoryRatio.setTextColorAsLinearGradient(arrayOf(
//                ContextCompat.getColor(context, R.color.light_blue_400),
//                ContextCompat.getColor(context, R.color.light_blue_300)
//            ))
        }
    }
}


