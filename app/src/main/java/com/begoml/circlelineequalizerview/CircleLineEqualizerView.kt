package com.begoml.circlelineequalizerview

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat

class CircleLineEqualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    private var lineWidth: Float = 0F
    private var background: Int = 0

    private var drawThread: CircleLineEqualizerDrawThread? = null

    init {
        holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                destroyed()
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                drawThread = CircleLineEqualizerDrawThread(getHolder()).apply {
                    initData(
                        lineWidth = lineWidth,
                        width = width,
                        height = height
                    )
                    start()
                    drawThread = this
                }
            }
        })
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        lineWidth = width * LINE_WIDTH
    }

    fun start() {
        drawThread?.isVisualizationEnabled = true
    }

    fun stop() {
        drawThread?.isVisualizationEnabled = false
    }

    private fun destroyed() {
        var retry = true
        // завершаем работу потока
        drawThread?.isRunning = false
        drawThread?.stopAnimation()
        while (retry) {
            try {
                drawThread?.join()
                retry = false
            } catch (e: InterruptedException) { // если не получилось, то будем пытаться еще и еще
            }
        }
    }

    fun setViewBackgroundResource(it: Int) {
        background = it

        if (it == 0) return
        drawThread?.setBackgroundColor(ContextCompat.getColor(context, background))
    }

    companion object {
        const val LINE_WIDTH = 0.0075F //todo why?
    }
}