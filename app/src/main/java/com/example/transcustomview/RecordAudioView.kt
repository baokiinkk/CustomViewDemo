package com.example.transcustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class RecordAudioView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    companion object {
        const val PARENT_KEY = "PARENT_KEY"
        const val CHILD_TOP_KEY = "CHILD_TOP_KEY"
        const val CHILD_BOTTOM_KEY = "CHILD_BOTTOM_KEY"
        const val CHILD_LEFT_KEY = "CHILD_LEFT_KEY"
        const val CHILD_RIGHT_KEY = "CHILD_RIGHT_KEY"
    }

    private var isRestored: Boolean = false

    // size parent
    private var rectParent = Rect()
    private var rectChildLeft = Rect()
    private var rectChildRight = Rect()
    private var rectChildTop = Rect()
    private var rectChildBottom = Rect()
    private var xTouch = 0f
    private var yTouch = 0f
    private var leftTouch = 0f
    private var rightTouch = 0f
    private var topTouch = 0f
    private var bottomTouch = 0f

    private val paint = Paint()
    private var isTouchleft = false
    private var isTouchRight = false
    private var isTouchTop = false
    private var isTouchBottom = false
    private var isTouchParent = false
    private var lineParentColor: Int = Color.WHITE
    private var lineChildColor: Int = Color.WHITE
    private var withParent = 0f
    private var heightParent = 0f
    private var styleEnum: Paint.Style = Paint.Style.STROKE


    init {
        initAttr(attrs)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(PARENT_KEY, rectParent)
        bundle.putParcelable(CHILD_LEFT_KEY, rectChildLeft)
        bundle.putParcelable(CHILD_RIGHT_KEY, rectChildRight)
        bundle.putParcelable(CHILD_TOP_KEY, rectChildTop)
        bundle.putParcelable(CHILD_BOTTOM_KEY, rectChildBottom)
        return CustomViewState(superState, bundle)
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is CustomViewState) {
            super.onRestoreInstanceState(state.superState)
            rectParent = state.bundle.getParcelable(PARENT_KEY) ?: Rect()
            rectChildLeft = state.bundle.getParcelable(CHILD_LEFT_KEY) ?: Rect()
            rectChildRight = state.bundle.getParcelable(CHILD_RIGHT_KEY) ?: Rect()
            rectChildTop = state.bundle.getParcelable(CHILD_TOP_KEY) ?: Rect()
            rectChildBottom = state.bundle.getParcelable(CHILD_BOTTOM_KEY) ?: Rect()
            isRestored = true
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRectParent(canvas)
        drawRectChild(canvas, rectChildLeft)
        drawRectChild(canvas, rectChildRight)
        drawRectChild(canvas, rectChildTop)
        drawRectChild(canvas, rectChildBottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (!isRestored) {
            isRestored = false
            val width = withParent
            val height = heightParent

            rectParent.setSize(0f, width, 0f, height)

            rectChildLeft.setSize(0f, 10f, height / 2 + 30, height / 2 - 30)

            rectChildRight.setSize(width - 10, width, height / 2 + 30, height / 2 - 30)

            rectChildTop.setSize(width / 2 - 30, width / 2 + 30, 0f, 10f)

            rectChildBottom.setSize(width / 2 - 30, width / 2 + 30, height - 10, height)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                xTouch = x
                yTouch = y
                leftTouch = rectParent.left
                rightTouch = rectParent.right
                topTouch = rectParent.top
                bottomTouch = rectParent.bottom
                if ((x in rectChildLeft.left - 20..rectChildLeft.right + 20) && (x in rectChildRight.left - 20..rectChildRight.right + 20)) {
                    isTouchleft = true
                    isTouchRight = true
                    return true
                } else if ((y in rectChildTop.top - 20..rectChildTop.bottom + 20) && (y in rectChildBottom.top - 20..rectChildBottom.bottom + 20)) {
                    isTouchTop = true
                    isTouchBottom = true
                    return true
                } else if (x in rectChildLeft.left - 20..rectChildLeft.right + 20) {
                    isTouchleft = true
                    return true
                } else if (x in rectChildRight.left - 20..rectChildRight.right + 20) {
                    isTouchRight = true
                    return true
                } else if (y in rectChildTop.top - 20..rectChildTop.bottom + 20) {
                    isTouchTop = true
                    return true
                } else if (y in rectChildBottom.top - 20..rectChildBottom.bottom + 20) {
                    isTouchBottom = true
                    return true
                } else if (x in rectParent.left - 20..rectParent.right + 20 && y in rectParent.top - 20..rectParent.bottom + 20) {
                    isTouchParent = true
                    return true
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (x in 0f..width.toFloat()) {
                    if (isTouchleft && x < rectParent.right + 30) {
                        rectParent.left = x
                        rectChildLeft.left = x
                        rectChildLeft.right = rectChildLeft.left + 10
                    }
                    if (isTouchRight && x > rectParent.left - 30) {
                        rectParent.right = x
                        rectChildRight.right = x
                        rectChildRight.left = rectChildRight.right - 10
                    }
                    updateTopBottomChildRect()
                }
                if (y in 0f..height.toFloat()) {
                    if (isTouchTop && y < rectParent.bottom - 30) {
                        rectParent.top = y
                        rectChildTop.top = y
                        rectChildTop.bottom = rectChildTop.top + 10
                    }
                    if (isTouchBottom && y > rectParent.top + 30) {
                        rectParent.bottom = y
                        rectChildBottom.bottom = y
                        rectChildBottom.top = rectChildBottom.bottom - 10
                    }
                    updateLeftRightChildRect()
                }
                if (isTouchParent) {
                    rectParent.left = leftTouch - (xTouch - x)
                    rectParent.right = rightTouch - (xTouch - x)
                    rectParent.top = topTouch - (yTouch - y)
                    rectParent.bottom = bottomTouch - (yTouch - y)

                    updateLeftRightChildRect(x)
                    updateTopBottomChildRect(y)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isTouchleft = false
                isTouchRight = false
                isTouchTop = false
                isTouchBottom = false
                isTouchParent = false
            }
        }

        return super.onTouchEvent(event)
    }

    private fun initAttr(attrs: AttributeSet?) {
        if (attrs != null) {

            val styleAttr = context.obtainStyledAttributes(attrs, R.styleable.CustomView)

            lineParentColor = if (styleAttr.hasValue(R.styleable.CustomView_lineColor)) {
                styleAttr.getColor(R.styleable.CustomView_lineColor, Color.WHITE)
            } else {
                Color.WHITE
            }

            lineChildColor = if (styleAttr.hasValue(R.styleable.CustomView_lineChildColor)) {
                styleAttr.getColor(R.styleable.CustomView_lineChildColor, Color.WHITE)
            } else {
                Color.WHITE
            }
            styleEnum = if (styleAttr.hasValue(R.styleable.CustomView_style)) {
                val value = styleAttr.getInt(R.styleable.CustomView_style, 0)
                getStyle(value)
            } else {
                Paint.Style.STROKE
            }

            withParent = if (styleAttr.hasValue(R.styleable.CustomView_widthViewLine)) {
                styleAttr.getDimension(R.styleable.CustomView_widthViewLine, 0f)
            } else {
                0f
            }
            heightParent = if (styleAttr.hasValue(R.styleable.CustomView_heightViewLine)) {
                styleAttr.getDimension(R.styleable.CustomView_widthViewLine, 0f)
            } else {
                0f
            }
        }
    }

    private fun getStyle(value: Int): Paint.Style {
        return when (value) {
            0 -> {
                Paint.Style.FILL
            }
            else -> {
                Paint.Style.STROKE
            }
        }
    }

    private fun updateTopBottomChildRect(y: Float = 0f) {
        rectChildBottom.left =
            (rectParent.right - rectParent.left) / 2 + 30 + rectParent.left
        rectChildBottom.right =
            (rectParent.right - rectParent.left) / 2 - 30 + rectParent.left
        rectChildTop.left =
            (rectParent.right - rectParent.left) / 2 + 30 + rectParent.left
        rectChildTop.right =
            (rectParent.right - rectParent.left) / 2 - 30 + rectParent.left

        rectChildTop.top = if (y != 0f) topTouch - (yTouch - y) else rectChildTop.top
        rectChildTop.bottom = if (y != 0f) rectChildTop.top + 10 else rectChildTop.bottom
        rectChildBottom.top = if (y != 0f) bottomTouch - (yTouch - y) else rectChildBottom.top
        rectChildBottom.bottom = if (y != 0f) rectChildBottom.top - 10 else rectChildBottom.bottom
    }

    private fun updateLeftRightChildRect(x: Float = 0f) {
        rectChildLeft.top =
            (rectParent.bottom - rectParent.top) / 2 - 30 + rectParent.top
        rectChildLeft.bottom =
            (rectParent.bottom - rectParent.top) / 2 + 30 + rectParent.top
        rectChildRight.top =
            (rectParent.bottom - rectParent.top) / 2 - 30 + rectParent.top
        rectChildRight.bottom =
            (rectParent.bottom - rectParent.top) / 2 + 30 + rectParent.top

        rectChildLeft.left = if (x != 0f) leftTouch - (xTouch - x) else rectChildLeft.left
        rectChildLeft.right = if (x != 0f) rectChildLeft.left + 10 else rectChildLeft.right
        rectChildRight.left = if (x != 0f) rightTouch - (xTouch - x) else rectChildRight.left
        rectChildRight.right = if (x != 0f) rectChildRight.left - 10 else rectChildRight.right
    }

    private fun drawRectParent(canvas: Canvas) {
        paint.color = lineParentColor
        paint.style = styleEnum
        paint.strokeWidth = 10f
        canvas.drawRect(rectParent.left, rectParent.top, rectParent.right, rectParent.bottom, paint)
    }

    private fun drawRectChild(canvas: Canvas, rect: Rect) {

        paint.color = lineChildColor
        paint.style = Paint.Style.FILL

        canvas.drawRect(
            rect.left,
            rect.top,
            rect.right,
            rect.bottom,
            paint
        )
    }
}

data class Rect(
    var left: Float = 0f,
    var top: Float = 0f,
    var right: Float = 0f,
    var bottom: Float = 0f
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(left)
        parcel.writeFloat(top)
        parcel.writeFloat(right)
        parcel.writeFloat(bottom)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun setSize(left: Float, right: Float, top: Float, bottom: Float) {
        this.left = left
        this.right = right
        this.top = top
        this.bottom = bottom
    }

    companion object CREATOR : Parcelable.Creator<Rect> {
        override fun createFromParcel(parcel: Parcel): Rect {
            return Rect(parcel)
        }

        override fun newArray(size: Int): Array<Rect?> {
            return arrayOfNulls(size)
        }
    }
}

class CustomViewState : View.BaseSavedState {
    val bundle: Bundle

    constructor(superState: Parcelable?, bundle: Bundle) : super(superState) {
        this.bundle = bundle
    }

    constructor(parcel: Parcel, classLoader: ClassLoader) : super(parcel) {
        bundle = parcel.readBundle(classLoader) ?: Bundle()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeBundle(bundle)
    }

    companion object CREATOR : Parcelable.Creator<CustomViewState> {
        override fun createFromParcel(parcel: Parcel): CustomViewState {
            return CustomViewState(parcel, CustomViewState::class.java.classLoader)
        }

        override fun newArray(size: Int): Array<CustomViewState?> {
            return arrayOfNulls(size)
        }
    }
}



