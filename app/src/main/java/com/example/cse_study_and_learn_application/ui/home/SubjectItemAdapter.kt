package com.example.cse_study_and_learn_application.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cse_study_and_learn_application.databinding.ItemSubjectCatBinding
import com.example.cse_study_and_learn_application.model.QuizCategory
import com.example.cse_study_and_learn_application.utils.dpToPx
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
class SubjectItemAdapter(private val questionRelateModels: List<QuizCategory>, private val listener: OnSubjectItemClickListener) : RecyclerView.Adapter<SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectCatBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        // 화면 너비의 절반과 화면 높이의 0.35배로 뷰의 크기를 조정
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
        val subject = questionRelateModels[position]

        // 과목 타이틀, 문제 수 설정
        holder.bind(subject)

        // 과목 이미지 설정
        try {
            val assetManager = holder.itemView.context.assets
            val inputStream = assetManager.open(subject.bg)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val inputStream2 = assetManager.open("images/bg/semi_white2.png")
            val bitmap2 = BitmapFactory.decodeStream(inputStream2)

            holder.setImage(bitmap, bitmap2)

        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 클릭 리스너 실행
        holder.itemView.setOnClickListener {
            listener.onSubjectItemClick(subject)
        }
    }

    override fun getItemCount(): Int = questionRelateModels.size
}

class SubjectViewHolder(private val binding: ItemSubjectCatBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(subject: QuizCategory) {
        binding.tvSubjectTitle.text = subject.title
        binding.tvQuestionCnt.text = subject.cnt
        binding.tvIcon.text = subject.icon
    }

    fun setImage(bitmap: Bitmap, bitmap2: Bitmap) {
        Glide.with(binding.root.context).load(bitmap).into(binding.ivSubjectBg)
        Glide.with(binding.root.context).load(bitmap2).into(binding.ivBlur)
        // binding.ivSubjectBg.setImageBitmap(bitmap)
    }
}