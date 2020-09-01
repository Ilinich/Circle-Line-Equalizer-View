package com.begoml.circlelineequalizerview

import android.animation.ArgbEvaluator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.SurfaceHolder
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.concurrent.fixedRateTimer

class CircleLineEqualizerDrawThread(private val surfaceHolder: SurfaceHolder) : Thread() {

    private val lines by lazy { mutableListOf<Line>() }
    private val colors by lazy { initColors() }

    private val random by lazy { ThreadLocalRandom.current() }

    private var lineWidth: Float = 0F
    private var width: Int = 0
    private var height: Int = 0

    @Volatile
    private var background: Int = 0

    private var startColor = 0xDB017D
    private var endColor = 0x253786

    // flag can draw lines or not
    @Volatile
    var isVisualizationEnabled: Boolean = false

    @Volatile
    var isRunning: Boolean = true

    // flag can change color for lines
    private var canChangeColor: Boolean = false

    private var timer: Timer? = null

    private val randomStartPoint
        get() = random.nextDouble(0.7, 0.9)

    override fun run() {
        timer?.cancel()
        startTimer()

        var canvas: Canvas?
        while (isRunning) {

            if (isVisualizationEnabled) {
                canvas = surfaceHolder.lockCanvas(null)

                drawColor(canvas)
                draw(canvas)

                canvas?.let {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }

                calculate()
            }
        }
    }

    private fun drawColor(canvas: Canvas) {
        if (background == 0) {
            canvas.drawColor(Color.BLACK)
        } else {
            canvas.drawColor(background)
        }
    }

    private fun draw(canvas: Canvas) {

        val isChangeColor = canChangeColor

        if (lines.isNotEmpty()) {

            var i = 0
            while (i < MAX_LINES) {

                val position = i * SIZE_LINES / MAX_LINES
                val line = lines[position]

                canvas.save()

                // rotate the canvas for some percent
                canvas.rotate((-i).toFloat(), (width / 2).toFloat(), (height / 2).toFloat())

                val startPoint = line.startPoint
                val endPoint = line.currentPoint

                // if we can change color , calculate new position for color
                val colorPosition = if (isChangeColor) {
                    var rotate = line.colorPosition + 1

                    if (rotate >= SIZE_LINES) {
                        rotate = 0
                    }

                    rotate
                } else {
                    line.colorPosition
                }


                line.colorPosition = colorPosition

                // get color for current position
                val paint = colors[line.colorPosition]

                // draw new line
                canvas.drawLine(
                    startPoint,
                    startPoint,
                    endPoint,
                    startPoint,
                    paint
                )

                // return back canvas
                canvas.restore()

                i += MAX_LINES / SIZE_LINES
            }
        }

        if (isChangeColor) {
            canChangeColor = false
        }
    }

    private fun calculate() {
        lines.forEach {
            it.calculateNextStep()
        }
    }

    /**
     * interpolator for time when can change color
     */
    private fun startTimer() {
        timer = fixedRateTimer(period = 80L, initialDelay = 80L) {
            canChangeColor = true
        }
    }

    /**
     * initialization of lines with start points
     */
    private fun initLinesIfNeed() {
        if (lines.isNotEmpty()) return

        val startPoint = width / 2F

        var currentAngle = ANGLE

        for (i in 0 until SIZE_LINES) {

            lines.add(
                Line(
                    startPoint = startPoint,
                    percent = randomStartPoint,
                    colorPosition = i
                )
            )

            currentAngle += ANGLE
        }
    }

    /**
     * initialization of colors for lines
     */
    private fun initColors(): List<Paint> {
        val colors = mutableListOf<String>()
        val step = 1.0F / (SIZE_LINES / 2)
        var fraction = 0F

        val partSize = SIZE_LINES / 2

        var index = 0;
        while (index != partSize) {
            val color = ArgbEvaluator().evaluate(fraction, startColor, endColor) as Int
            colors.add(color.toHex())
            fraction += step
            index++
        }

        val secondPartColors = mutableListOf<String>().apply {
            addAll(colors)
            reverse()
        }

        colors.addAll(secondPartColors)

        return colors.map {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                isAntiAlias = true
                color = Color.parseColor(it)
                strokeWidth = lineWidth
            }
        }
    }

    fun initData(lineWidth: Float, width: Int, height: Int) {
        this.lineWidth = lineWidth
        this.width = width
        this.height = height

        initLinesIfNeed()

        lines.forEach {
            it.start()
        }

        canChangeColor = false
    }

    fun stopAnimation() {
        lines.forEach {
            it.stop()
        }

        timer?.cancel()
        canChangeColor = false
        isVisualizationEnabled = false
    }

    fun setBackgroundColor(background: Int) {
        this.background = background

        surfaceHolder.lockCanvas(null)?.let { canvas ->
            drawColor(canvas)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    companion object {
        const val ANGLE = 2F
        const val SIZE_LINES = 180

        const val MAX_LINES = 360
    }
}

private fun Int.toHex() = String.format("#%08X", this).replace("#00", "#")