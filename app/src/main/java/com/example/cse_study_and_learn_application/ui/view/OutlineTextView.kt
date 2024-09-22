package com.example.cse_study_and_learn_application.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.cse_study_and_learn_application.R

class OutlineTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val textPaint: Paint = Paint()
    private val strokePaint: Paint = Paint()

    init {
        textPaint.style = Paint.Style.FILL
        textPaint.color = ContextCompat.getColor(context, android.R.color.white) // 기본 텍스트 색상
        textPaint.isAntiAlias = true

        strokePaint.style = Paint.Style.STROKE
        strokePaint.color = ContextCompat.getColor(context, R.color.light_blue_600) // 기본 테두리 색상
        strokePaint.isAntiAlias = true
        strokePaint.strokeWidth = 40f // 테두리 두께 조절

        // 커스텀 폰트 적용 (res/font 폴더에서 로드)
        val customFont = ResourcesCompat.getFont(context, R.font.hakgyoansim_dunggeunmiso_b)
        textPaint.typeface = customFont
        strokePaint.typeface = customFont
    }

    // 텍스트와 테두리 색상을 동적으로 변경하는 메서드
    fun setColors(textColor: Int, strokeColor: Int) {
        textPaint.color = textColor
        strokePaint.color = strokeColor
        invalidate() // 뷰 다시 그리기
    }

    override fun onDraw(canvas: Canvas) {
        val text = text.toString()
        textPaint.textSize = textSize
        strokePaint.textSize = textSize

        // 테두리 그리기
        canvas.drawText(text, paddingLeft.toFloat(), baseline.toFloat(), strokePaint)
        // 텍스트 그리기
        canvas.drawText(text, paddingLeft.toFloat(), baseline.toFloat(), textPaint)
    }
}