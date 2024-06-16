package com.example.cse_study_and_learn_application.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.cse_study_and_learn_application.R

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
        paint!!.color = resources.getColor(R.color.black)
        paint!!.strokeWidth = 8f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (line in lines) {
            canvas.drawLine(line.startX, line.startY, line.endX, line.endY, paint!!)
        }
    }

    fun addLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        lines.add(Line(startX, startY, endX, endY))
        invalidate()
    }

    fun removeLine(startX: Float, startY: Float, endX: Float, endY: Float) {
        lines.remove(Line(startX, startY, endX, endY))
        invalidate()
    }

    private data class Line(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float
    )
}
