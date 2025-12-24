package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.WireframeSphereRenderer

@Composable
fun WireframeSphereView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)

                val renderer = WireframeSphereRenderer()
                setRenderer(renderer)

                // Continuous rendering required for the idle animation (auto-rotation)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                var previousX = 0f
                var previousY = 0f

                var isScaling = false

                val scaleDetector = ScaleGestureDetector(
                    context,
                    object :
                        ScaleGestureDetector.SimpleOnScaleGestureListener() {
                        override fun onScale(detector: ScaleGestureDetector): Boolean {
                            renderer.zoom(detector.scaleFactor)
                            return true
                        }
                    })

                setOnTouchListener { _, event ->
                    scaleDetector.onTouchEvent(event)

                    if (scaleDetector.isInProgress) {
                        return@setOnTouchListener true
                    }

                    when (event.actionMasked) {
                        MotionEvent.ACTION_DOWN -> {
                            isScaling = false
                            previousX = event.x
                            previousY = event.y
                        }

                        MotionEvent.ACTION_POINTER_DOWN -> {
                            isScaling = true
                        }

                        MotionEvent.ACTION_MOVE -> {
                            if (!isScaling) {
                                val dx = event.x - previousX
                                val dy = event.y - previousY

                                // Rotation Scale Factor
                                // Adjust sensitivity as needed
                                val touchScaleFactor = 0.5f

                                // Note: Dragging horizontally (x change) rotates around Y axis
                                // Dragging vertically (y change) rotates around X axis
                                renderer.rotate(
                                    dx * touchScaleFactor,
                                    dy * touchScaleFactor
                                )

                                previousX = event.x
                                previousY = event.y
                            }
                        }
                    }
                    true
                }
            }
        }
    )
}
