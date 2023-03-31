package com.hanuszczak.loadingstatusbaranimation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
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
    private val textPaint = Paint().apply{
        textSize = context.resources.getDimension(R.dimen.default_text_size)
        textAlign = Paint.Align.CENTER
    }
    private val circlePaint = Paint().apply{}

    private var text: String = ""

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(
        ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {
                text = "We are loading"
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    duration = 2500L
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        progressValue = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(progressValue, 1F).apply {
                    duration = 100L
                    addUpdateListener {
                        progressValue = it.animatedValue as Float
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            text = "Click to download"
                            progressValue = 0.0F
                            invalidate()
                        }
                    })
                    start()
                }
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
            drawRectangle(canvas)
            drawText(canvas)
            drawArc(canvas)
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
            heightSize.toFloat() / 2 + 10,
            textPaint
        )
    }

    private fun drawArc(canvas: Canvas) {
        val circleRadius = resources.getDimension(R.dimen.default_circle_radius)
        circlePaint.color = Color.GREEN
        val xPosition = 3 * widthSize.toFloat() / 4
        val yPosition = heightSize.toFloat() / 2
        canvas.drawArc(
            xPosition,
            yPosition - circleRadius,
            circleRadius * 2 + xPosition,
            circleRadius + yPosition,
            0F,
            360F * progressValue,
            true,
            circlePaint
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