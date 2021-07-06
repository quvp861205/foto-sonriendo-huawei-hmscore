package com.pquinonezv.hselfiecamera.overlay


import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.pquinonezv.hselfiecamera.camera.CameraConfiguration

class GraphicOverlay(
    context: Context,
    atts: AttributeSet?
) : View(context, atts) {

    private val lock = Any()
    private var previewWidth = 0
    private var previewHeight = 0
    var widtScaleValue = 1.0f
        private set
    var heightScaleValue = 1.0f
        private set
    var cameraFacing = CameraConfiguration.CAMERA_FACING_FRONT
        private set
    private val graphics: MutableList<BaseGraphic> = ArrayList()

    fun addGraphic(graphic: BaseGraphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        this.postInvalidate()
    }

    fun setCameraInfo(width: Int, height: Int, facing: Int) {
        synchronized(lock) {
            previewWidth = width
            previewHeight = height
            cameraFacing = facing
        }
        this.postInvalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock) {
            if (previewWidth != 0 && previewHeight != 0) {
                widtScaleValue =
                    width.toFloat() / previewWidth.toFloat()
                heightScaleValue =
                    height.toFloat() / previewHeight.toFloat()
            }
            for (graphic in graphics) {
                graphic.draw(canvas)
            }
        }
    }
}