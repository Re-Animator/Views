package com.reanimator.analogclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalogClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mWidth = 0
    private var mHeight = 0

    private val mClockHours = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    /** spacing and padding of the clock-hands around the clock round */
    private var mPadding = 0
    private val mNumeralSpacing = 0

    /** truncation of the heights of the clock-hands */
    private var mHandTruncation = 0
    private var mHourHandTruncation = 0

    private var mRadius = 0
    private var mPaint = Paint()
    private val mRect = Rect()
    private var isInit = false

    private val mHandsStandardThickness = 4f
    private var mHandsThicknessFactor = 0f
    private var mHandsThickness = 0f

    private var mSecondHandColor = Color.RED

    private val thicknessTypes = listOf("thin", "normal", "thick")

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AnalogClockView,
            defStyleAttr,
            0
        ).apply {
            try {
                mHandsThicknessFactor =
                    getInteger(R.styleable.AnalogClockView_handsStyle, 1).toFloat()
                mSecondHandColor =
                    Color.parseColor(getString(R.styleable.AnalogClockView_secondHandColor))
            } finally {
                recycle()
            }
        }

        mHandsThickness = mHandsStandardThickness * mHandsThicknessFactor
        mPadding = mNumeralSpacing + 50
    }

    override fun onDraw(canvas: Canvas) {
        /** initialize necessary values  */
        if (!isInit) {
            mWidth = width
            mHeight = height
            //mPadding = mNumeralSpacing + 50 // spacing from the circle border

            val minAttr = min(mHeight, mWidth)
            mRadius = minAttr / 2 - mPadding

            // for maintaining different heights among the clock-hands
            mHandTruncation = minAttr / 20
            mHourHandTruncation = minAttr / 10

            isInit = true
        }

        drawClockCircleBorder(canvas)
        drawClockCentre(canvas)
        drawClockHours(canvas)
        drawHands(canvas)

        /** invalidate the appearance for next representation of time   */
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun setPaintAttributes(
        color: Int = Color.BLACK,
        style: Paint.Style = Paint.Style.STROKE,
        strokeWidth: Float = 4f
    ) {
        mPaint.reset()
        mPaint.color = color
        mPaint.style = style
        mPaint.strokeWidth = strokeWidth
        mPaint.isAntiAlias = true
    }

    private fun drawClockCircleBorder(canvas: Canvas) {
        setPaintAttributes(Color.BLACK, Paint.Style.STROKE, 4f)
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mRadius + mPadding - 10).toFloat(),
            mPaint
        )
    }

    private fun drawClockCentre(canvas: Canvas) {
        setPaintAttributes(style = Paint.Style.FILL)
        canvas.drawCircle(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            8f,
            mPaint
        )
    }

    private fun drawClockHours(canvas: Canvas) {
        val fontSize =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                14f,
                resources.displayMetrics
            ).toInt()
        mPaint.textSize = fontSize.toFloat()

        for (hour in mClockHours) {
            val drawnHour = hour.toString()
            mPaint.getTextBounds(
                drawnHour,
                0,
                drawnHour.length,
                mRect
            )

            val angle = Math.PI / 6 * (hour - 3)
            val x = (mWidth / 2 + cos(angle) * mRadius - mRect.width() / 2).toInt()
            val y = (mHeight / 2 + sin(angle) * mRadius + mRect.height() / 2).toInt()

            canvas.drawText(
                hour.toString(),
                x.toFloat(),
                y.toFloat(),
                mPaint
            )
        }
    }

    private fun drawHands(canvas: Canvas) {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR].toFloat()

        setPaintAttributes(strokeWidth = mHandsStandardThickness * mHandsThicknessFactor)

        drawHandLine(
            canvas,
            ((hour + calendar[Calendar.MINUTE] / 60) * 5f).toDouble(),
            isHour = true,
            isSecond = false
        )
        drawHandLine(
            canvas,
            calendar[Calendar.MINUTE].toDouble(),
            isHour = false,
            isSecond = false
        )
        drawHandLine(
            canvas,
            calendar[Calendar.SECOND].toDouble(),
            isHour = false,
            isSecond = true
        )
    }

    private fun drawHandLine(
        canvas: Canvas,
        moment: Double,
        isHour: Boolean,
        isSecond: Boolean
    ) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius =
            if (isHour) {
                mRadius - mHandTruncation - mHourHandTruncation
            } else
                mRadius - mHandTruncation

        if (isSecond) mPaint.color = mSecondHandColor

        canvas.drawLine(
            mWidth.toFloat() / 2,
            mHeight.toFloat() / 2,
            (mWidth / 2 + cos(angle) * handRadius).toFloat(),
            (mHeight / 2 + sin(angle) * handRadius).toFloat(),
            mPaint
        )
    }

    /**
     * Returns the color in Int format of hand of the clock
     *
     *  @return int second hand color
     */
    fun getSecondHandColor(): Int = mSecondHandColor

    /**
     * Returns the color in Int format of hand of the clock
     *
     *  @throws IllegalArgumentException
     */
    fun setSecondHandColor(color: String) {
        mSecondHandColor = Color.parseColor(color)
        invalidate()
        requestLayout()
    }

    /**
     * Returns the width of clock's hands in string format
     *
     *  @return width of hands
     */
    fun getHandsThickness() = when (mHandsThicknessFactor) {
        1f -> "thin"
        3f -> "normal"
        else -> "thick"
    }

    /**
     * Sets the hands width for this view
     *
     *  @param string hands thickness
     *  @throws IllegalArgumentException
     */
    fun setHandsThickness(thicknessType: String) {
        if (thicknessTypes.contains(thicknessType)) {
            mHandsThicknessFactor = when (thicknessType) {
                "thin" -> 1f
                "normal" -> 3f
                else -> 5f
            }
            invalidate()
            requestLayout()
        } else {
            throw IllegalArgumentException("Illegal argument for setHandsThickness")
        }
    }
}