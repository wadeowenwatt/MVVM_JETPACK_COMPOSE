package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.opengl.GLSurfaceView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.touchpad.TouchPad2DRenderer

@Composable
fun TouchPad2DView(modifier: Modifier = Modifier) {
    val anim = remember { Animatable(0f) }
    val renderer = remember { TouchPad2DRenderer() }

    LaunchedEffect(Unit) {
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 3000,
                easing = LinearEasing,
            )
        )
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)
                setEGLConfigChooser(8, 8, 8, 8, 16, 0)
                holder.setFormat(android.graphics.PixelFormat.TRANSLUCENT)
                setZOrderOnTop(true)
                
                setRenderer(renderer)

                // Continuous rendering required for the idle animation (auto-rotation)
                renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                setOnTouchListener { _, event ->
                    true
                }
            }
        },
        update = {
            if (anim.value < 1f) {
                renderer.zoom(1.02f)
            }
        }
    )
}