package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer.sphere

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import wade.owen.watts.base_jetpack.ui.pages.calendar.shader.NeonShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WireframeSphereRenderer : GLSurfaceView.Renderer {

    // Rotation state
    @Volatile
    private var angleX: Float = 0f
    @Volatile
    private var angleY: Float = 0f

    // Auto-rotation speed (degrees per frame approx)
    private val autoRotationSpeed = 0.2f

    // Matrices
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // OpenGL Handles
    private var programId: Int = 0
    private var mvpMatrixHandle: Int = 0
    private var timeHandle: Int = 0
    private var isPointHandle: Int = 0
    private var cameraDistanceHandle: Int = 0

    private val vao = IntArray(1)
    private val vbo = IntArray(1)
    private val ibo = IntArray(1)

    // Data
    private var indexCount = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set clear color to Dark Gray for debugging (to confirm GL is running)
        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
        
        // Disable depth test for wireframe to avoid z-fighting issues
        GLES30.glDisable(GLES30.GL_DEPTH_TEST)

        // This will enable blending using for render glow effect
        GLES30.glEnable(GLES30.GL_BLEND)
        GLES30.glBlendFunc(
            GLES30.GL_ONE,
            GLES30.GL_ONE
        )
        
        // Use thick lines
        GLES30.glLineWidth(5f) 

        // 1. Compile Shaders
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, NeonShader.VERTEX_SHADER)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, NeonShader.FRAGMENT_SHADER_WITH_TOGGLE)

        programId = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
            
            // Link Status Check
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(it, GLES30.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                GLES30.glDeleteProgram(it)
                throw RuntimeException("Program Link Failed: " + GLES30.glGetProgramInfoLog(it))
            }
        }

        // 2. Get Uniform Handles
        mvpMatrixHandle = GLES30.glGetUniformLocation(programId, "uMVPMatrix")
        timeHandle = GLES30.glGetUniformLocation(programId, "uTime")
        isPointHandle = GLES30.glGetUniformLocation(programId, "uIsPoint")
        cameraDistanceHandle = GLES30.glGetUniformLocation(programId, "uCameraDistance")

        // 3. Generate Sphere Data
        val (vertices, indices) = SphereGridGenerator.generateCustomSphere(1.0f)
        indexCount = indices.size

        // 4. Setup Buffers
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertices)
        vertexBuffer.position(0)

        val indexBuffer = ByteBuffer.allocateDirect(indices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(indices)
        indexBuffer.position(0)

        // 5. Setup VAO
        GLES30.glGenVertexArrays(1, vao, 0)
        GLES30.glGenBuffers(1, vbo, 0)
        GLES30.glGenBuffers(1, ibo, 0)

        GLES30.glBindVertexArray(vao[0])

        // VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GLES30.GL_STATIC_DRAW)

        // IBO
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo[0])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, indexBuffer, GLES30.GL_STATIC_DRAW)

        // Vertex Attributes
        val stride = 7 * 4
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, stride, 0)

        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, stride, 3 * 4)

        GLES30.glBindVertexArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        
        // Use PerspectiveM for standard FOV 
        Matrix.perspectiveM(projectionMatrix, 0, 45f, ratio, 0.1f, 100f)
        
        // Camera setup (LookAt)
        // Position camera at z=cameraDistance
        updateViewMatrix()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)
        
        GLES30.glUseProgram(programId)

        // Time
        val time = (SystemClock.uptimeMillis() % 10000L) / 1000.0f
        GLES30.glUniform1f(timeHandle, time)

        // Camera Distance
        GLES30.glUniform1f(cameraDistanceHandle, cameraDistance)

        // Auto Rotation
        angleY += autoRotationSpeed

        // Model Matrix
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f)
        Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f)

        // MVP
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw
        GLES30.glBindVertexArray(vao[0])

        // Lines
        GLES30.glUniform1i(isPointHandle, 0)
        GLES30.glDrawElements(GLES30.GL_LINES, indexCount, GLES30.GL_UNSIGNED_INT, 0)

        // Points
        GLES30.glUniform1i(isPointHandle, 1)
        GLES30.glDrawElements(GLES30.GL_POINTS, indexCount, GLES30.GL_UNSIGNED_INT, 0)

        GLES30.glBindVertexArray(0)
    }

    // Interaction Methods

    /**
     * Updates rotation angles based on touch drag.
     */
    fun rotate(deltaX: Float, deltaY: Float) {
        angleX += deltaY
        angleY += deltaX
    }

    private var cameraDistance = 50f // Initial distance

    fun zoom(zoomFactor: Float) {
        cameraDistance /= zoomFactor
        // Clamp distance to reasonable limits
        if (cameraDistance < 7f) cameraDistance = 7f
        if (cameraDistance > 50f) cameraDistance = 50f
        updateViewMatrix()
    }

    private fun updateViewMatrix() {
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, cameraDistance, 0f, 0f, 0f, 0f, 2f, 0f)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
            val compiled = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                GLES30.glDeleteShader(shader)
                throw RuntimeException("Shader Error: " + GLES30.glGetShaderInfoLog(shader))
            }
        }
    }
}
