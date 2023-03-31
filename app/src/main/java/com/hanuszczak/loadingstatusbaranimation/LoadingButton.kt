package com.hanuszczak.loadingstatusbaranimation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressValue = 0.0F

    private val paint = Paint().apply{}
    private val textPaint = Paint().apply{}

    private var text: String = ""

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(
        ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {
                text = "We are loading"
            }
            ButtonState.Completed -> {
                text = "Click to download"
            }
        }
    }


    init {
        progressValue = 0.0F
        text = "Click to download"
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            canvas.drawColor(Color.RED)
            drawText(canvas)
            drawRectangle(canvas)
        }
    }

    private fun drawRectangle(canvas: Canvas) {
        val rectDraw = RectF(
            0F,
            0F,
            widthSize.toFloat() * progressValue,
            heightSize.toFloat())

        paint.color = Color.BLUE
        canvas.drawRect(rectDraw, paint)
    }

    private fun drawText(canvas: Canvas) {
        textPaint.color = Color.WHITE
        canvas.drawText(
            text,
            widthSize.toFloat() / 2,
            heightSize.toFloat() / 2,
            textPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}