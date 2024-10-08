package com.cslu.cse_study_and_learn_application.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cslu.cse_study_and_learn_application.databinding.ItemSubjectCatBinding
import com.cslu.cse_study_and_learn_application.model.QuizCategory
import com.cslu.cse_study_and_learn_application.utils.Lg
import com.cslu.cse_study_and_learn_application.utils.dpToPx
import java.io.IOException

/**
 * Subject cat adapter
 *
 * @property questionRelateModels
 * @constructor Create empty Subject cat adapter
 * @author kjy
 * @since 2024-03-09
 *
 */
class SubjectItemAdapter(
    private var questionRelateModels: MutableList<QuizCategory>,
    private val listener: OnSubjectItemClickListener
) : RecyclerView.Adapter<SubjectViewHolder>(), Filterable {

    private var filteredQuestionRelateModels: MutableList<QuizCategory> = questionRelateModels

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 화면 너비의 절반과 화면 높이의 0.30배로 뷰의 크기를 조정
        val displayMetrics = parent.context.resources.displayMetrics
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = (displayMetrics.heightPixels * 0.30).toInt()

        val layoutParams = RecyclerView.LayoutParams(width, height)
        val margin = parent.context.dpToPx(5)
        layoutParams.setMargins(margin, margin, margin, margin)

        binding.root.layoutParams = layoutParams
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = filteredQuestionRelateModels[position]

        // 과목 타이틀, 문제 수 설정
        holder.bind(subject)

        /**
         * Init subject contents
         *ASD;FKLJLKNVXCZ
         *
         * L;SDAKJFL;K
         * VXCZLVLKANDLPVK
         *
         *
         *
         * FDALKGA;LSKDJG
         * V
         * DA
         * FGAS
         * DGQ
         * WERASDVXZC
         * V
         * XZCV
         * AS
         * DF
         * QWERASDV
         *
         */
        // 과목 이미지 설정
        try {
            val assetManager = holder.itemView.context.assets
            val inputStream = assetManager.open(subject.bg)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val inputStream2 = assetManager.open("images/bg/semi_white3.png")
            val bitmap2 = BitmapFactory.decodeStream(inputStream2)

            holder.setImage(bitmap, bitmap2)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 클릭 리스너 실행
        holder.itemView.setOnClickListener {
            listener.onSubjectItemClick(subject)
        }

        // 나중에 쓸지도 모름
        // 중심 좌표 계산 및 로그 출력
        holder.itemView.post {
            val location = IntArray(2)
            holder.itemView.getLocationOnScreen(location)

            val parentLocation = IntArray(2)
            (holder.itemView.parent as View).getLocationOnScreen(parentLocation)
            Lg.d("test", SubjectViewHolder::class.java.simpleName,
                "ParentView location: X: ${parentLocation[0]}, Y: ${parentLocation[1]}")


            // val centerX = location[0] + holder.itemView.width / 2
            // val centerY = location[1] + holder.itemView.height / 2
//
            // Lg.d("test", SubjectViewHolder::class.java.simpleName,"Item $position center: X: $centerX, Y: $centerY")
        }
    }

    override fun getItemCount(): Int = filteredQuestionRelateModels.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateItem(contents: MutableList<QuizCategory>) {
        questionRelateModels = contents
        filteredQuestionRelateModels = contents
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                filteredQuestionRelateModels = if (charString.isEmpty()) {
                    questionRelateModels
                } else {
                    questionRelateModels.filter {
                        it.title.contains(charString, ignoreCase = true)
                    }.toMutableList()
                }
                val filterResults = FilterResults()
                filterResults.values = filteredQuestionRelateModels
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredQuestionRelateModels = results?.values as MutableList<QuizCategory>
                notifyDataSetChanged()
            }
        }
    }
}

class SubjectViewHolder(private val binding: ItemSubjectCatBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(subject: QuizCategory) {
        binding.tvSubjectTitle.text = subject.title
        binding.tvQuestionCnt.text = subject.cnt
        binding.tvIcon.text = subject.icon

        // 텍스트 크기 조정 로직
        binding.tvSubjectTitle.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            val textView = binding.tvSubjectTitle
            if (textView.lineCount > 1) {
                textView.textSize = 16f // 원하는 크기로 줄이기
            } else {
                textView.textSize = 19f // 기본 크기
            }
        }
    }

    fun setImage(bitmap: Bitmap, bitmap2: Bitmap) {
        Glide.with(binding.root.context).load(bitmap).into(binding.ivSubjectBg)
        Glide.with(binding.root.context).load(bitmap2).into(binding.ivBlur)
    }
}
