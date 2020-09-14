package com.shahryar.airbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap

class AirBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val mAttrs = context.obtainStyledAttributes(attrs, R.styleable.AirBar)
    private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mLeft = 0F
    private var mTop = 200F
    private var mRight = mLeft
    private var mBottom = 0F
    private var isVirgin = true
    private val mProgressRect = RectF()
    private var mListener: OnProgressChangedListener? = null

    var max: Double = mAttrs.getInt(R.styleable.AirBar_max, 100).toDouble()
    var min: Double = mAttrs.getInt(R.styleable.AirBar_min, 0).toDouble()

    var progressBarFillColor: Int = mAttrs.getResourceId(
        R.styleable.AirBar_progressBarFillColor,
        resources.getColor(R.color.defaultLevel)
    )
        set(value) {
            field = value
            progressBarColor0 = value
            progressBarColor1 = value
            invalidate()
        }

    var backgroundCornerRadius: Float =
        mAttrs.getFloat(R.styleable.AirBar_backgroundCornerRadius, 50F)
        set(value) {
            field = value
            invalidate()
        }

    var backgroundFillColor: Int = mAttrs.getColor(
        R.styleable.AirBar_backgroundFillColor,
        resources.getColor(R.color.defaultBackground)
    )
        set(value) {
            field = value
            invalidate()
        }

    var icon: Drawable? = mAttrs.getDrawable(R.styleable.AirBar_icon)
        set(value) {
            field = value
            invalidate()
        }

    var progressBarColor0: Int =
        mAttrs.getResourceId(R.styleable.AirBar_progressBarColor0, progressBarFillColor)
        set(value) {
            field = value
            invalidate()
        }

    var progressBarColor1: Int =
        mAttrs.getResourceId(R.styleable.AirBar_progressBarColor1, progressBarFillColor)
        set(value) {
            field = value
            invalidate()
        }

    fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
        mListener = listener
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        invalidate()
    }

    /**
     * Draw background
     */
    override fun draw(canvas: Canvas?) {
        setBackgroundColor(backgroundFillColor)
        //Set rounded corner frame
        canvas?.clipPath(
            getRoundedRect(
                0F,
                0F,
                mRight,
                mBottom,
                backgroundCornerRadius,
                backgroundCornerRadius,
                true,
                true,
                true,
                true
            )!!
        )
        super.draw(canvas)
    }

    /**
     * Draw icon
     */
    override fun onDrawForeground(canvas: Canvas?) {
        val bitmap = icon?.toBitmap()
        if (bitmap != null && canvas != null) {
            val centerX: Float =
                canvas.width.toDouble().div(2.00).toFloat() - bitmap.width.toDouble().div(2.00)
                    .toFloat()
            canvas.drawBitmap(
                bitmap,
                centerX,
                mBottom - (bitmap.height.toDouble() * 1.5).toFloat(),
                mPaint
            )
        }
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onDraw(canvas: Canvas?) {

        mPaint.color = progressBarFillColor
        mPaint.style = Paint.Style.FILL
        mPaint.shader =
            LinearGradient(
                0F,
                0F,
                0F,
                height.toFloat(),
                progressBarColor0,
                progressBarColor1,
                Shader.TileMode.MIRROR
            )

        //First init of level rect
        if (isVirgin) {
            mLeft = 0F
            mTop = 200F
            mRight = mLeft + width
            mBottom = height + 0F
            mProgressRect.top = mTop
            mProgressRect.left = mLeft
            mProgressRect.bottom = mBottom
            mProgressRect.right = mRight
        }

        canvas?.drawRect(mProgressRect, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_MOVE) {
            isVirgin = false
            when {
                event.y in 0.0..mBottom.toDouble() -> mProgressRect.top = event.y
                event.y > 100 -> mProgressRect.top = mBottom
                event.y < 0 -> mProgressRect.top = 0F
            }
            mListener?.onProgressChanged(this, getProgress(), getPercentage())
            invalidate()
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            mListener?.afterProgressChanged(this, getProgress(), getPercentage())
        }
        return true
    }

    /**
     * Calculate percentage
     */
    private fun getPercentage(): Double {
        return String.format(
            "%.2f",
            (100 - ((mProgressRect.top.toDouble() / mBottom.toDouble()) * 100))
        ).toDouble()
    }

    /**
     * Calculate progress
     */
    private fun getProgress(): Double {
        return String.format("%.2f", ((((max - min) * getPercentage()) / 100.00) + min)).toDouble()
    }

    /**
     * @author Moh Mah at https://stackoverflow.com/a/35668889/10315711
     */
    private fun getRoundedRect(
        left: Float, top: Float, right: Float, bottom: Float, rx: Float, ry: Float,
        tl: Boolean, tr: Boolean, br: Boolean, bl: Boolean
    ): Path? {
        var rx = rx
        var ry = ry
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0f, -ry)
            path.rLineTo(-rx, 0f)
        }
        path.rLineTo(-widthMinusCorners, 0f)
        if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0f)
            path.rLineTo(0f, ry)
        }
        path.rLineTo(0f, heightMinusCorners)
        if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0f, ry)
            path.rLineTo(rx, 0f)
        }
        path.rLineTo(widthMinusCorners, 0f)
        if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0f)
            path.rLineTo(0f, -ry)
        }
        path.rLineTo(0f, -heightMinusCorners)

        path.close() //Given close, last lineto can be removed.
        return path
    }

    interface OnProgressChangedListener {
        fun onProgressChanged(airBar: AirBar, progress: Double, percentage: Double)
        fun afterProgressChanged(airBar: AirBar, progress: Double, percentage: Double)
    }
}