package wade.owen.watts.base_jetpack.ui.pages.calendar.shader

object NeonShader {

    /**
     * Vertex Shader
     * - Transforms position with MVP matrix.
     * - Passes color to fragment shader.
     * - Sets point size for GL_POINTS.
     */
    val VERTEX_SHADER = """
        #version 300 es
        precision mediump float;
        
        layout(location = 0) in vec4 aPosition;
        layout(location = 1) in vec4 aColor;
        
        uniform mat4 uMVPMatrix;
        uniform float uTime; 
        
        out vec4 vColor;
        
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vColor = aColor;
            
            // Massive point size to allow for large glow
            // Base 80.0 + pulsing +/- 20.0
            gl_PointSize = 80.0 + 20.0 * sin(uTime * 3.0); 
        }
    """.trimIndent()

    val FRAGMENT_SHADER = """
        #version 300 es
        precision mediump float;
        // Legacy shader, use WITH_TOGGLE instead
        in vec4 vColor;
        out vec4 fragColor;
        void main() { fragColor = vColor; } 
    """.trimIndent()

    val FRAGMENT_SHADER_WITH_TOGGLE = """
        #version 300 es
        precision mediump float;
        
        in vec4 vColor;
        uniform float uTime;
        uniform bool uIsPoint;
        
        out vec4 fragColor;
        
        void main() {
            vec4 baseColor = vColor;
        
            if (uIsPoint) {
                vec2 uv = gl_PointCoord - vec2(0.5);
                float dist = length(uv) * 2.0; // 0..1
        
                if (dist > 1.0) discard;
        
                // White core
                float core = 1.0 - smoothstep(0.0, 0.25, dist);
        
                // Glow falloff
                float glow = exp(-dist * 3.0);
                glow *= 0.8 + 0.2 * sin(uTime * 6.0);
        
                float alpha = clamp(max(core, glow), 0.0, 1.0);
        
                vec3 rgb =
                    core * vec3(1.0) +
                    glow * baseColor.rgb * 1.5;
        
                rgb *= alpha;
        
                fragColor = vec4(rgb, alpha);
            } else {
                float pulse = 0.85 + 0.15 * sin(uTime * 5.0);
                fragColor = vec4(baseColor.rgb * pulse, baseColor.a);
            }
        }
    """.trimIndent()
}
