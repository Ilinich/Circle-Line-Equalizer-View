package com.begoml.circlelineequalizerview

class Line(
    val startPoint: Float,
    var percent: Double,
    var colorPosition: Int
) {

    private val linePoints by lazy { mutableListOf<Float>() }

    private var step = 0

    private var isIncrement = true
    private var isVisualizationEnabled = false

    init {
        var percent = ONE_STEP

        linePoints.add(startPoint)

        while (percent < FINISH_STEP) {
            val step = (startPoint * (percent / FINISH_STEP.toFloat()))
            val point = startPoint + step

            linePoints.add(point)

            percent += ONE_STEP
        }

        step = findFirstStep
    }

    val currentPoint: Float
        get() = linePoints[step]

    private val findFirstStep
        get() = (linePoints.size * percent).toInt()

    private val isFirstStep
        get() = step == 0


    fun calculateNextStep() {
        if (!isVisualizationEnabled && isFirstStep) {
            return
        }

        if (!isVisualizationEnabled) {
            step--
        } else {
            if (isIncrement) {
                step++
            } else {
                step--
            }
        }

        val pointsSize = linePoints.size
        if (step >= pointsSize) {
            step = pointsSize - 1
        }

        if (step == linePoints.lastIndex || !isVisualizationEnabled) {
            isIncrement = false
        } else if (step <= findFirstStep) {
            isIncrement = true
        }
    }

    fun start() {
        isVisualizationEnabled = true
        isIncrement = true
    }

    fun stop() {
        isVisualizationEnabled = false
    }

    companion object {
        private const val ONE_STEP = 1
        private const val FINISH_STEP = 110
    }
}