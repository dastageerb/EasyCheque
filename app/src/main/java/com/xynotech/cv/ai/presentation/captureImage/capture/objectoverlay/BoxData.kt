package com.xynotech.cv.ai.presentation.captureImage.capture.objectoverlay

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.NonNull
import com.google.mlkit.vision.objects.DetectedObject

data class BoxData(val text: String, val boundingBox: Rect)

class ObjectOverlay(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val lock = Any()

    private val graphics = mutableSetOf<BoxData>()

    private val rectPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 2 * resources.displayMetrics.density
    }

    private val textBackgroundPaint = Paint().apply {
        color = Color.parseColor("#66000000")
        style = Paint.Style.FILL
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 20 * resources.displayMetrics.density
    }

    private val rect = RectF()
    private val offset = resources.displayMetrics.density * 8
    private val round = resources.displayMetrics.density * 4

    private var scale: Float = 1f

    private var xOffset: Float = 0f
    private var yOffset: Float = 0f

    fun setSize(imageWidth: Int, imageHeight: Int) {
        val overlayRatio = width / height.toFloat()
        val imageRatio = imageWidth / imageHeight.toFloat()
        if (overlayRatio < imageRatio) {
            // 同じ高さにしたとき overlay の方が 幅 が小さいので height に合わせる
            scale = height / imageHeight.toFloat()

            xOffset = (imageWidth * scale - width) * 0.5f
            yOffset = 0f
        } else {
            // 同じ高さにしたとき overlay の方が 幅 が大きいので width に合わせる
            scale = width / imageWidth.toFloat()

            xOffset = 0f
            yOffset = (imageHeight * scale - height) * 0.5f
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        synchronized(lock) {
            for (graphic in graphics) {
                rect.set(
                    graphic.boundingBox.left * scale,
                    graphic.boundingBox.top * scale,
                    graphic.boundingBox.right * scale,
                    graphic.boundingBox.bottom * scale
                )

                rect.offset(-xOffset, -yOffset)

                canvas.drawRect(rect, rectPaint)

                if (graphic.text.isNotEmpty()) {
                    canvas.drawRoundRect(
                        rect.left,
                        rect.bottom - offset,
                        rect.left + offset + textPaint.measureText(graphic.text) + offset,
                        rect.bottom + textPaint.textSize + offset,
                        round,
                        round,
                        textBackgroundPaint
                    )
                    canvas.drawText(
                        graphic.text,
                        rect.left + offset,
                        rect.bottom + textPaint.textSize,
                        textPaint
                    )
                }
            }
        }
    }

    fun set(list: List<BoxData>) {
        synchronized(lock) {
            graphics.clear()
            for (boxData in list) {
                graphics.add(boxData)
            }
        }
        postInvalidate()
    }
}

class DetectedObjects(
    rotationDegrees: Int,
    @NonNull image: Bitmap,
    val objects: List<DetectedObject>
) {
    val imageWidth: Int
    val imageHeight: Int

    init {
        when (rotationDegrees) {
            90, 270 -> {
                imageWidth = image.height
                imageHeight = image.width
            }
            else -> {
                imageWidth = image.width
                imageHeight = image.height
            }
        }
    }
}

