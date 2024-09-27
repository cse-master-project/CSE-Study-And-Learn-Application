package com.cslu.cse_study_and_learn_application.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cslu.cse_study_and_learn_application.R

class LineDrawingView : View {
    private var paint: Paint? = null
    private var lines: MutableList<Line> = mutableListOf()

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        paint = Paint()
        // paint!!.color = resources.getColor(R.color.black)
        paint!!.color = ContextCompat.getColor(context, R.color.light_blue_600)

        paint!!.strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (line in lines) {
            paint!!.color = line.color  // 각 선의 색상을 설정
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint!!)
        }
    }
    fun clearLines() {
        lines.clear()
        invalidate()
    }


    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float, color: Int) {
        lines.add(Line(startX, startY, endX, endY, color))  // 색상 지정
        invalidate()  // 다시 그리기
    }


    fun removeLine(startX: Float, startY: Float, endX: Float, endY: Float, color: Int) {
        lines.remove(Line(startX, startY, endX, endY, color))
        invalidate()
    }

    private data class Line(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val color: Int // 색상을 추가
    )
}
