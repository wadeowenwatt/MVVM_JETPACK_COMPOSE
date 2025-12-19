package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CubeRenderer : GLSurfaceView.Renderer {

    // Rotation angles in degrees
    @Volatile
    private var angleX: Float = 0f
    @Volatile
    private var angleY: Float = 0f

    // Matrices
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val projectionMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    // Handlers
    private var programId: Int = 0
    private var mvpMatrixHandle: Int = 0
    private val vao = IntArray(1)
    private val vbo = IntArray(1)
    private val ibo = IntArray(1)

    // Cube Data
    // Position (X, Y, Z) and Color (R, G, B, A)
    // 8 vertices
    private val vertexData = floatArrayOf(
        // Front face
        -0.5f, 0.5f, 0.5f,   1.0f, 0.0f, 0.0f, 1.0f, // 0 Top-left (Red)
        -0.5f, -0.5f, 0.5f,  0.0f, 1.0f, 0.0f, 1.0f, // 1 Bottom-left (Green)
        0.5f, -0.5f, 0.5f,   0.0f, 0.0f, 1.0f, 1.0f, // 2 Bottom-right (Blue)
        0.5f, 0.5f, 0.5f,    1.0f, 1.0f, 0.0f, 1.0f, // 3 Top-right (Yellow)
        // Back face
        -0.5f, 0.5f, -0.5f,  0.0f, 1.0f, 1.0f, 1.0f, // 4 Top-left (Cyan)
        -0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 1.0f, 1.0f, // 5 Bottom-left (Magenta)
        0.5f, -0.5f, -0.5f,  1.0f, 1.0f, 1.0f, 1.0f, // 6 Bottom-right (White)
        0.5f, 0.5f, -0.5f,   0.0f, 0.0f, 0.0f, 1.0f  // 7 Top-right (Black)
    )

    private val indexData = intArrayOf(
        0, 1, 2, 0, 2, 3,    // Front
        3, 2, 6, 3, 6, 7,    // Right
        7, 6, 5, 7, 5, 4,    // Back
        4, 5, 1, 4, 1, 0,    // Left
        4, 0, 3, 4, 3, 7,    // Top
        1, 5, 6, 1, 6, 2     // Bottom
    )

    // Shaders
    private val vertexShaderCode = """
        #version 300 es
        layout(location = 0) in vec4 aPosition;
        layout(location = 1) in vec4 aColor;
        uniform mat4 uMVPMatrix;
        out vec4 vColor;
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vColor = aColor;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        #version 300 es
        precision mediump float;
        in vec4 vColor;
        out vec4 fragColor;
        void main() {
            fragColor = vColor;
        }
    """.trimIndent()

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.1f, 0.1f, 0.1f, 1.0f) // Dark background
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Compile and Link Program
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode)
        programId = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        // Get Handle
        mvpMatrixHandle = GLES30.glGetUniformLocation(programId, "uMVPMatrix")

        // Buffers
        val vertexBuffer = ByteBuffer.allocateDirect(vertexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexData)
        vertexBuffer.position(0)

        val indexBuffer = ByteBuffer.allocateDirect(indexData.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .put(indexData)
        indexBuffer.position(0)

        // Generate VAO, VBO, IBO
        GLES30.glGenVertexArrays(1, vao, 0)
        GLES30.glGenBuffers(1, vbo, 0)
        GLES30.glGenBuffers(1, ibo, 0)

        // Bind VAO
        GLES30.glBindVertexArray(vao[0])

        // Bind and buffer VBO
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vbo[0])
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, vertexData.size * 4, vertexBuffer, GLES30.GL_STATIC_DRAW)

        // Bind and buffer IBO
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ibo[0])
        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexData.size * 4, indexBuffer, GLES30.GL_STATIC_DRAW)

        // Enable Vertex Attributes
        // Position (vec3) -> stride 7 * 4 bytes
        GLES30.glEnableVertexAttribArray(0)
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 7 * 4, 0)

        // Color (vec4) -> stride 7 * 4 bytes, offset 3 * 4 bytes
        GLES30.glEnableVertexAttribArray(1)
        GLES30.glVertexAttribPointer(1, 4, GLES30.GL_FLOAT, false, 7 * 4, 3 * 4)

        // Unbind VAO
        GLES30.glBindVertexArray(0)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        val ratio: Float = width.toFloat() / height.toFloat()
        // Frustum: -ratio, ratio, -1, 1, 3, 7
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
        
        // Define Camera View
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 5f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        GLES30.glUseProgram(programId)

        // Calculate Model Matrix based on rotation
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.rotateM(modelMatrix, 0, angleX, 1f, 0f, 0f) // Rotate around X axis
        Matrix.rotateM(modelMatrix, 0, angleY, 0f, 1f, 0f) // Rotate around Y axis

        // Calculate MVP = Projection * View * Model
        val mvMatrix = FloatArray(16)
        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        // Pass Uniform
        GLES30.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        // Draw with VAO
        GLES30.glBindVertexArray(vao[0])
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexData.size, GLES30.GL_UNSIGNED_INT, 0)
        GLES30.glBindVertexArray(0)
    }

    fun setRotation(dx: Float, dy: Float) {
        angleX += dy
        angleY += dx
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES30.glCreateShader(type).also { shader ->
            GLES30.glShaderSource(shader, shaderCode)
            GLES30.glCompileShader(shader)
            
            // Optional: Check compile status
            val compiled = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0)
            if (compiled[0] == 0) {
                 GLES30.glDeleteShader(shader)
                 throw RuntimeException("Could not compile shader $type: " + GLES30.glGetShaderInfoLog(shader))
            }
        }
    }
}
