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
        out float vBrightness;
        
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            
            // Depth-based brightness attenuation
            // Camera is at Z=7, Sphere Radius=1.
            // Closest point (front) is at Z=1 (distance 6), Farthest (back) is at Z=(-1) (distance 8).
            // gl_Position.w is approx distance from camera.
            // Map distance 6.0 -> brightness 1.0
            // Map distance 8.0 -> brightness 0.1
            // smoothstep(far_edge, near_edge, value)
            float brightness = smoothstep(8.1, 6.0, gl_Position.w);
            
            // Ensure minimum brightness so back vertices aren't totally invisible
            // User requested "even fainter" -> reduced from 0.2 to 0.05
            brightness = max(0.01, brightness);
            vBrightness = brightness;
            
            vColor = vec4(aColor.rgb * brightness, aColor.a);
            
            // Massive point size to allow for large glow
            // Base 80.0 + pulsing +/- 20.0
            gl_PointSize = 80.0 + 20.0 * sin(0.5 * 3.0); 
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
        in float vBrightness;
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
                glow *= 0.8 + 0.2 * sin(0.5 * 6.0);
        
                float alpha = clamp(max(core, glow), 0.0, 1.0);
        
                vec3 rgb =
                    core * vec3(1.0) * vBrightness +
                    glow * baseColor.rgb * 1.5;
        
                rgb *= alpha;
        
                fragColor = vec4(rgb, alpha);
            } else {
                float pulse = 0.85 + 0.15 * sin(0.5 * 5.0);
                fragColor = vec4(baseColor.rgb * pulse, baseColor.a);
            }
        }
    """.trimIndent()
}
