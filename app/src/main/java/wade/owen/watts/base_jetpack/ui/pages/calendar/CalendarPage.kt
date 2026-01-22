package wade.owen.watts.base_jetpack.ui.pages.calendar

import android.opengl.GLSurfaceView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

import android.view.MotionEvent
import wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.sphere.IcosahedronRenderer
import android.content.Context
import android.view.Choreographer
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import com.google.android.filament.Skybox
import com.google.android.filament.utils.ModelViewer
import java.nio.ByteBuffer
import com.google.android.filament.utils.Utils
import java.nio.ByteOrder
import com.google.android.filament.LightManager
import com.google.android.filament.EntityManager

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import kotlin.random.Random

@Composable
fun CalendarPage(modifier: Modifier = Modifier) {
    var sphereColor by remember { mutableStateOf(Color.Cyan) }

    Box(modifier) {
        WireframeSphereView(
            modifier = modifier,
            sphereColor = sphereColor
        )
//        TouchPad2DView(modifier)

        Button(
            onClick = {
                sphereColor = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 1f
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp)
        ) {
            Text("Change Color")
        }
    }
}

@Composable
fun Cube3DView(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            GLSurfaceView(context).apply {
                setEGLContextClientVersion(3)
                val renderer = IcosahedronRenderer(context)
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


@Composable
fun Blender3DModelView(
    assetFileName: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            FilamentModelView(context, assetFileName)
        }
    )
}


private class FilamentModelView(
    context: Context,
    private val assetFileName: String
) : SurfaceView(context), Choreographer.FrameCallback {

    private var modelViewer: ModelViewer? = null
    private val choreographer = Choreographer.getInstance()

    private val lightEntities = IntArray(3)

    init {
        // Essential: Initialize Filament Utils
        Utils.init()
        
        // Initialize Filament model viewer
        modelViewer = ModelViewer(this)

        // Enable Bloom
        val view = modelViewer!!.view
        view.bloomOptions = view.bloomOptions.apply {
            enabled = true
        }

        // Create colored lights
        createColoredLights()

        // Load the GLB file
        loadGlb()

        // Set background to black for better contrast with the colored lights
        modelViewer?.scene?.skybox = Skybox.Builder()
            .color(0.0f, 0.0f, 0.0f, 1.0f)
            .build(modelViewer!!.engine)

        // Make it full screen in the view or transform to unit cube
        modelViewer?.transformToUnitCube()
    }

    private fun createColoredLights() {
        val engine = modelViewer!!.engine
        val entityManager = EntityManager.get()
        entityManager.create(lightEntities)

        // 1. Magenta Light (Top Left)
        LightManager.Builder(LightManager.Type.POINT)
            .color(1.0f, 0.0f, 1.0f) // Magenta
            .intensity(50_000.0f) // High intensity for bloom
            .position(-2.0f, 2.0f, 2.0f)
            .falloff(10.0f)
            .build(engine, lightEntities[0])

        // 2. Cyan Light (Bottom Right)
        LightManager.Builder(LightManager.Type.POINT)
            .color(0.0f, 1.0f, 1.0f) // Cyan
            .intensity(50_000.0f)
            .position(2.0f, -2.0f, 2.0f)
            .falloff(10.0f)
            .build(engine, lightEntities[1])

        // 3. Yellow Light (Top Right)
        LightManager.Builder(LightManager.Type.POINT)
            .color(1.0f, 1.0f, 0.0f) // Yellow
            .intensity(50_000.0f)
            .position(2.0f, 2.0f, -2.0f)
            .falloff(10.0f)
            .build(engine, lightEntities[2])

        // Add lights to the scene
        modelViewer?.scene?.addEntities(lightEntities)
        
        // IMPORTANT: Disable the default IBL (which is white) so our colored lights show up
        modelViewer?.scene?.indirectLight?.intensity = 0.0f
    }

    private fun loadGlb() {
        try {
            val assets = context.assets
            assets.open(assetFileName).use { input ->
                val bytes = input.readBytes()
                val buffer = ByteBuffer.allocateDirect(bytes.size)
                buffer.order(ByteOrder.nativeOrder())
                buffer.put(bytes)
                buffer.flip()
                modelViewer?.loadModelGlb(buffer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        choreographer.postFrameCallback(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        choreographer.removeFrameCallback(this)
        
        // Destroy lights
        val engine = modelViewer?.engine
        val entityManager = EntityManager.get()
        engine?.let {
            it.destroyEntity(lightEntities[0])
            it.destroyEntity(lightEntities[1])
            it.destroyEntity(lightEntities[2])
        }
        entityManager.destroy(lightEntities)
        
        modelViewer?.destroyModel()
        modelViewer = null
    }

    override fun doFrame(frameTimeNanos: Long) {
        choreographer.postFrameCallback(this)
        
        // Auto-rotate the model if possible
        modelViewer?.asset?.root?.let { rootEntity ->
            val transformManager = modelViewer!!.engine.transformManager
            val instance = transformManager.getInstance(rootEntity)
            
            // Apply continuous rotation around Y axis
            val transform = FloatArray(16)
            transformManager.getTransform(instance, transform)
            
            // Rotate 0.5 degrees per frame
            android.opengl.Matrix.rotateM(transform, 0, 0.5f, 0f, 1f, 0f)
            transformManager.setTransform(instance, transform)
        }

        modelViewer?.render(frameTimeNanos)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        modelViewer?.onTouchEvent(event)
        return true
    }
}