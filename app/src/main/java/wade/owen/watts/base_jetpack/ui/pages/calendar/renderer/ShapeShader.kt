package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

object ShapeShader {
    val VERTEX_SHADER_CODE = """
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

    val FRAGMENT_SHADER_CODE = """
        #version 300 es
        precision mediump float;
        in vec4 vColor;
        out vec4 fragColor;
        void main() {
            fragColor = vColor;
        }
    """.trimIndent()
}
