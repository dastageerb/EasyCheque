package com.xynotech.cv.ai.presentation.captureImage.capture

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.xynotech.converso.ai.R

class RectBox constructor(context: Context?, attributeSet: AttributeSet?) :
    View(context, attributeSet) {

    private var rect:Rect? = null
    private var paint = Paint().apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context!!, R.color.color_green)
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rect?.let { canvas.drawRect(it, paint) }
    }

    public fun drawRect(rect:Rect) {
        paint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, R.color.color_green)
            strokeWidth = 10f
        }
        this.rect = rect
        invalidate()
    }


    fun drawBlack(rect: Rect) {
        paint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, android.R.color.black)
            strokeWidth = 10f
        }
        this.rect = rect
        invalidate()
    }

    fun removeFrame() {
        paint = Paint().apply {
            style = Paint.Style.STROKE
            color = ContextCompat.getColor(context!!, android.R.color.transparent)
            strokeWidth = 10f
        }
        invalidate()
    }

}