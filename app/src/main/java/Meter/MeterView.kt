package Meter

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class MeterView(context: Context, attributes: AttributeSet): SurfaceView(context, attributes), SurfaceHolder.Callback {
    private val meterThread: MeterThread

    init {
        holder.addCallback(this)

        meterThread = MeterThread(holder, this)
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        meterThread.setRunning(true)
        meterThread.start()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        var retry = true
        while (retry) {
            try {
                meterThread.setRunning(false)
                meterThread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            retry = false
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {

    }

    fun update() {

    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

}