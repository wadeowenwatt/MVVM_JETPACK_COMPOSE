package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.opengl.GLSurfaceView
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import android.view.MotionEvent
import wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.IcosahedronRenderer

@Composable
fun CalendarPage(modifier: Modifier = Modifier) {
    Cube3DView(modifier)
}

@Composable
fun Cube3DView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)
                val renderer = IcosahedronRenderer()
                setRenderer(renderer)
                renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

                var previousX = 0f
                var previousY = 0f

                setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            previousX = event.x
                            previousY = event.y
                        }

                        MotionEvent.ACTION_MOVE -> {
                            val dx = event.x - previousX
                            val dy = event.y - previousY

                            // factor to scale the rotation
                            val touchScaleFactor = 180.0f / 320
                            renderer.setRotation(
                                dx * touchScaleFactor,
                                dy * touchScaleFactor
                            )
                            requestRender()

                            previousX = event.x
                            previousY = event.y
                        }
                    }
                    true
                }
            }
        }
    )
}