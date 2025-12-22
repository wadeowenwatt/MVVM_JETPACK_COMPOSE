package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import wade.owen.watts.base_jetpack.ui.pages.calendar.shader.ShapeShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class IcosahedronRenderer : GLSurfaceView.Renderer {

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

    // Icosahedron Data
    // Golden Ratio
    private val phi = 1.618f

    // Vertices (Position X, Y, Z + Color R, G, B, A)
    // 12 Vertices
    private val vertexData = floatArrayOf(
        // (0, ±1, ±phi)
        0f, 1f, phi,   1.0f, 0.0f, 0.0f, 1.0f, // 0 Red
        0f, 1f, -phi,  1.0f, 0.5f, 0.0f, 1.0f, // 1 Orange
        0f, -1f, phi,  1.0f, 1.0f, 0.0f, 1.0f, // 2 Yellow
        0f, -1f, -phi, 0.0f, 1.0f, 0.0f, 1.0f, // 3 Green

        // (±1, ±phi, 0)
        1f, phi, 0f,   0.0f, 1.0f, 1.0f, 1.0f, // 4 Cyan
        1f, -phi, 0f,  0.0f, 0.0f, 1.0f, 1.0f, // 5 Blue
        -1f, phi, 0f,  0.5f, 0.0f, 1.0f, 1.0f, // 6 Violet
        -1f, -phi, 0f, 1.0f, 0.0f, 1.0f, 1.0f, // 7 Magenta

        // (±phi, 0, ±1)
        phi, 0f, 1f,   1.0f, 0.5f, 0.5f, 1.0f, // 8 Salmon
        phi, 0f, -1f,  0.5f, 1.0f, 0.5f, 1.0f, // 9 Light Green
        -phi, 0f, 1f,  0.5f, 0.5f, 1.0f, 1.0f, // 10 Light Blue
        -phi, 0f, -1f, 1.0f, 0.5f, 0.0f, 1.0f  // 11 Dark Orange
    )

    // Indices for Lines (Wireframe)
    // 30 Edges
    private val indexData = intArrayOf(
        // 0 is connected to 2, 4, 6, 8, 10
        0, 2,  0, 4,  0, 6,  0, 8,  0, 10,
        // 1 is connected to 3, 5, 7, 9, 11
        1, 3,  1, 4,  1, 6,  1, 9,  1, 11, // Wait, 1 is (0, 1, -phi). 4 is (1, phi, 0). 6 is (-1, phi, 0).
        // Let's verify standard connectivity relative to rectangles.
        // Rectangle 1: 0(0,1,phi), 2(0,-1,phi), 1(0,1,-phi), 3(0,-1,-phi)
        // Edges: (0,2), (1,3) are the short sides? No, dimensions are different.
        // The 3 Golden Rectangles are on XY, YZ, ZX planes.
        // Vertices 0,1,2,3 are on YZ plane (x=0).
        // Vertices 4,5,6,7 are on XY plane (z=0).
        // Vertices 8,9,10,11 are on ZX plane (y=0).

        // Edges around the rectangles
        // YZ Plane rectangle tops/bottoms
        0, 2, 1, 3, // Vertical sides of YZ rect? No.
        // Let's rely on standard Icosahedron edge list.
        // 12 vertices. 20 faces. 30 edges.
        
        // From vertex 0 (0,1,phi): neighbors are 2, 8, 10, 4, 6?
        // 0-2 (short edge of rect), 0-8, 0-10, 0-4, 0-6 ?
        // Let's correct edges.
        // Group by golden rectangles?
        // 1. (0, 1, phi) & (0, -1, phi) -> 0-2
        // 2. (0, 1, -phi) & (0, -1, -phi) -> 1-3
        // 3. (1, phi, 0) & (-1, phi, 0) -> 4-6
        // 4. (1, -phi, 0) & (-1, -phi, 0) -> 5-7
        // 5. (phi, 0, 1) & (phi, 0, -1) -> 8-9
        // 6. (-phi, 0, 1) & (-phi, 0, -1) -> 10-11
        // These are the "short" edges of the golden rectangles so to speak? No, distance is 2.
        
        // Let's list edges properly.
        // 5 neighbors for each vertex.
        
        // 0 (0,1,phi): 2 (0,-1,phi), 8 (phi,0,1), 10 (-phi,0,1), 4 (1,phi,0) - NO, 4 is far? (1, 1.6, 0). dist sq = 1 + 0.36 + 2.6 = ~4. Matches edge length 2 (sq=4).
        // 0 neighbors: 2, 8, 10, 4, 6.
        0, 2,  0, 8,  0, 10, 0, 4,  0, 6,
        
        // 1 (0,1,-phi): 3 (0,-1,-phi), 9 (phi,0,-1), 11 (-phi,0,-1), 4 (1,phi,0), 6 (-1,phi,0).
        1, 3,  1, 9,  1, 11, 1, 4,  1, 6,
        
        // 2 (0,-1,phi): (Already 0), 8, 10, 5 (1,-phi,0), 7 (-1,-phi,0).
        2, 8,  2, 10, 2, 5,  2, 7,
        
        // 3 (0,-1,-phi): (Already 1), 9, 11, 5, 7.
        3, 9,  3, 11, 3, 5,  3, 7,
        
        // 4 (1,phi,0): (Already 0, 1), 8, 9, 5?
        // 4 (1,phi,0) - 8 (phi,0,1). dist sq = (1-1.6)^2 + (1.6)^2 + 1 = 0.36 + 2.56 + 1 = 3.92 approx 4. Yes.
        // Neighbors of 4: 0, 1, 8, 9, 5? No, 5 is (1, -phi, 0), dist is 2*phi ~ 3.2. Not an edge.
        // 4 neighbors: 0, 1, 8, 9 ... and 5? No. The 5th neighbor?
        // Let's re-verify 4's neighbors.
        // 4 is (1, phi, 0).
        // 0 (0,1,phi), 1 (0,1,-phi), 8 (phi,0,1)? (phi-1)^2 + (0-phi)^2 + 1^2 = .38 + 2.6 + 1 = 4. Yes.
        // 9 (phi,0,-1)? (phi-1)^2 + phi^2 + 1 = 4. Yes.
        // 6 (-1,phi,0)? dist=2. Yes.
        // So 4 connects to: 0, 1, 8, 9, 6.
        // 0,1,6 already connected earlier.
        4, 8,  4, 9,  4, 6, 
        
        // 5 (1,-phi,0): 2, 3, 8, 9, 7(-1,-phi,0).
        5, 8,  5, 9,  5, 7, // (2,3 connected earlier)
        
        // 6 (-1,phi,0): 0, 1, 10, 11, 4.
        // (0, 1, 4 connected earlier).
        6, 10, 6, 11,
        
        // 7 (-1,-phi,0): 2, 3, 10, 11, 5.
        // (2, 3, 5 connected earlier).
        7, 10, 7, 11,
        
        // 8 (phi,0,1): 0, 2, 4, 5, 9? 
        // 8 (phi,0,1) - 9 (phi,0,-1) dist 2. Yes.
        // (0,2,4,5 connected earlier).
        8, 9,
        
        // 10 (-phi,0,1): 0, 2, 6, 7, 11?
        // 10 (-phi,0,1) - 11 (-phi,0,-1) dist 2. Yes.
        // (0,2,6,7 connected earlier).
        10, 11,
        
        // 2 edges left? Total edges 30.
        // Count:
        // 0: 5 edges
        // 1: 5 edges (10 total)
        // 2: 4 new edges (14 total)
        // 3: 4 new edges (18 total)
        // 4: 3 new edges (21 total)
        // 5: 3 new edges (24 total)
        // 6: 2 new edges (26 total)
        // 7: 2 new edges (28 total)
        // 8: 1 new edge (29 total)
        // 10: 1 new edge (30 total)
        // 9, 11 neighbors exhausted.
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.0f, 0.0f, 0.0f, 0.0f) // Transparent/Black background
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        // Compile and Link Program
        val vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, ShapeShader.VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, ShapeShader.FRAGMENT_SHADER_CODE)
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
        // Frustum
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
        
        // Define Camera View
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 4f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        GLES30.glUseProgram(programId)
        
        // Enable Line Width - Note: Some drivers ignore widths > 1
        GLES30.glLineWidth(10f) 

        // Calculate Model Matrix based on rotation
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.scaleM(modelMatrix, 0, 0.6f, 0.6f, 0.6f) // Make shape smaller
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
        // Draw Lines
        GLES30.glDrawElements(GLES30.GL_LINES, indexData.size, GLES30.GL_UNSIGNED_INT, 0)
        
        // Draw Points (Vertices) for glowing effect simulation
        // GLES30.glDrawElements(GLES30.GL_POINTS, indexData.size, GLES30.GL_UNSIGNED_INT, 0)
        
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
