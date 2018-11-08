package Meter

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas

class MeterHandle(var image: Bitmap) {
    var x: Int = 0
    var y: Int = 0
    var w: Int = 0
    var h: Int = 0
    private var velocity = 20
    private val screenWidth = Resources.getSystem().displayMetrics.widthPixels
    private val screenHeight = Resources.getSystem().displayMetrics.heightPixels

    init {
        w = image.width
        h = image.height

        x = screenWidth/2
        y = screenHeight/2
    }

    fun draw(canvas: Canvas) {
        canvas.drawBitmap(image, x.toFloat(), y.toFloat(), null)
    }

    fun update(pressure: Float) {

    }
}