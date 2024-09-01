package com.example.cse_study_and_learn_application.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import com.example.cse_study_and_learn_application.R

@SuppressLint("ViewConstructor")
class HighlightView(
    context: Context,
    private val targetRect: Rect,
    private val helpText: String,
    private val showPosition: HighlightPosition, // 강조 위치 결정 변수
    private val heightThreshold: Int,
    private val bubbleMargin: Float, // 말풍선 마진
    private val bubblePadding: Float, // 말풍선 패딩
    private val scaleFactor: Float // 크기 비율 변수
) : View(context) {

    private val paint = Paint().apply {
        color = Color.BLACK
        alpha = 200
        isAntiAlias = true
    }

    private val textPaint = TextPaint().apply {
        color = Color.BLACK
        textSize = 60f // 텍스트 크기를 60f로 설정
        isAntiAlias = true
    }

    private val borderColor = ContextCompat.getColor(context, R.color.light_blue_500) // XML 리소스에서 테두리 색상 가져오기

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        val cornerRadius = 20f // 모서리 둥글기 반경

        val halfWidth = (targetRect.width() / 2) * scaleFactor
        val halfHeight = (targetRect.height() / 2) * scaleFactor
        val expandedRect = RectF(
            targetRect.centerX() - halfWidth,
            targetRect.centerY() - halfHeight - heightThreshold,
            targetRect.centerX() + halfWidth,
            targetRect.centerY() + halfHeight - heightThreshold
        )

        canvas.drawRoundRect(
            expandedRect,
            cornerRadius,
            cornerRadius,
            paint
        )

        paint.xfermode = null

        // 말풍선 배경 그리기
        val bubblePaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val borderPaint = Paint().apply {
            color = borderColor // 테두리 색상 설정
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        // 말풍선의 위치 계산
        val screenWidth = width.toFloat()

        // StaticLayout을 사용하여 텍스트 높이를 계산
        val textLayout = StaticLayout(helpText, textPaint, (screenWidth - 2 * bubbleMargin - 2 * bubblePadding).toInt(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)
        val bubbleHeight = textLayout.height + 2 * bubblePadding

        val bubbleRect = when (showPosition) {
            HighlightPosition.SCREEN_TOP -> RectF(bubbleMargin, bubbleMargin, screenWidth - bubbleMargin, bubbleMargin + bubbleHeight)
            HighlightPosition.SCREEN_BOTTOM -> RectF(bubbleMargin, height - bubbleMargin - bubbleHeight, screenWidth - bubbleMargin, height - bubbleMargin)
            HighlightPosition.UI_TOP -> RectF(bubbleMargin, expandedRect.top - bubbleHeight - bubbleMargin, screenWidth - bubbleMargin, expandedRect.top - bubbleMargin)
            HighlightPosition.UI_BOTTOM -> RectF(bubbleMargin, expandedRect.bottom + bubbleMargin, screenWidth - bubbleMargin, expandedRect.bottom + bubbleHeight + bubbleMargin)
        }

        canvas.drawRoundRect(bubbleRect, cornerRadius, cornerRadius, bubblePaint)
        canvas.drawRoundRect(bubbleRect, cornerRadius, cornerRadius, borderPaint)

        // 텍스트 위치 계산에서 패딩 반영
        canvas.save()
        canvas.translate(bubbleRect.left + bubblePadding, bubbleRect.top + bubblePadding)
        textLayout.draw(canvas)
        canvas.restore()
    }
}
