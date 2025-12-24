package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.opengl.GLSurfaceView
import android.view.MotionEvent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.sphere.WireframeSphereRenderer

@Composable
fun WireframeSphereView(modifier: Modifier = Modifier) {
    val anim = remember { Animatable(0f) }
    val renderer = remember { WireframeSphereRenderer() }

    LaunchedEffect(Unit) {
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = LinearEasing,
            )
        )
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)
                setRenderer(renderer)
                
                // Continuous rendering required for the idle animation (auto-rotation)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                var previousX = 0f
                var previousY = 0f

                var isScaling = false

//                val scaleDetector = ScaleGestureDetector(
//                    context,
//                    object :
//                        ScaleGestureDetector.SimpleOnScaleGestureListener() {
//                        override fun onScale(detector: ScaleGestureDetector): Boolean {
//                            renderer.zoom(detector.scaleFactor)
//                            return true
//                        }
//                    })

                setOnTouchListener { _, event ->
//                    scaleDetector.onTouchEvent(event)
//
//                    if (scaleDetector.isInProgress) {
//                        return@setOnTouchListener true
//                    }

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
        },
        update = {
            if (anim.value < 1f) {
                renderer.zoom(1.05f)
            }
        }
    )
}
