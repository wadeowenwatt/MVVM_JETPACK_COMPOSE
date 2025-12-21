package wade.owen.watts.base_jetpack.ui.pages.calendar.renderer

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
        uniform float uTime; // Time uniform for pulsing size/effects if needed
        
        out vec4 vColor;
        
        void main() {
            gl_Position = uMVPMatrix * aPosition;
            vColor = aColor;
            
            // Dynamic point size based on depth (simple perspective attentuation simulation)
            // or just a fixed large size for the "glow" dot.
            // 10.0 base size, slightly pulsing
            gl_PointSize = 12.0 + 3.0 * sin(uTime * 3.0); 
        }
    """.trimIndent()

    /**
     * Fragment Shader
     * - Handles neon glow logic.
     * - Distinguishes conceptually between Points and Lines implicitly 
     *   (GL_POINTS render as squares/circles, GL_LINES as lines).
     * - We use gl_PointCoord to render circular soft dots.
     */
    val FRAGMENT_SHADER = """
        #version 300 es
        precision mediump float;
        
        in vec4 vColor;
        uniform float uTime;
        
        out vec4 fragColor;
        
        void main() {
            // Check if we are rendering a point (gl_PointCoord is defined).
            // For lines, gl_PointCoord is usually (0,0) or undefined behavior depending on driver, 
            // but in ES 3.0 for lines it's not useful. 
            // However, a common trick is to use a uniform to switch modes, or just rely on the shape.
            
            // To make points round and glowing:
            vec2 coord = gl_PointCoord - vec2(0.5);
            float distSq = dot(coord, coord);
            
            if (distSq > 0.25) {
                // If rendering POINTS, discard corners to make a circle.
                // If rendering LINES, gl_PointCoord might be constant?
                // Actually, gl_PointCoord is only valid for points. 
                // We should separate shaders or render passes if we want perfect safety.
                // But let's try a soft falloff assuming this shader is used for Points primarily,
                // or use a uniform 'uIsPoint'.
                
                // For this implementation, we will use a uniform or just simple alpha blending logic.
                // Let's rely on standard blending and discard only if we are sure.
                // But for a wireframe, we ignore this check usually for lines.
            }

            // Let's add a pulse to the alpha/brightness
            float pulse = 0.8 + 0.2 * sin(uTime * 2.0); // 0.6 to 1.0
            
            vec4 finalColor = vColor;
            finalColor.rgb *= pulse;
            
            // Standard point glow logic (circular), ONLY applies if meaningful point coord.
            // Since we use the same shader for lines, we need to be careful.
            // We'll rely on blending for the glow look without discarding fragments for lines.
            
            fragColor = finalColor;
        }
    """.trimIndent()
    
    // We will actually need two separate shaders or a uniform to toggle "Point Mode" vs "Line Mode"
    // to render round points vs solid lines correctly, OR just accept square points?
    // User asked for "Glowing dots". Square dots look bad.
    // I will add a uniform 'uIsPoint'.
    
    val FRAGMENT_SHADER_WITH_TOGGLE = """
        #version 300 es
        precision mediump float;
        
        in vec4 vColor;
        uniform float uTime;
        uniform bool uIsPoint; // true for points, false for lines
        
        out vec4 fragColor;
        
        void main() {
            vec4 baseColor = vColor;
            
            // Add global pulse
            float pulse = 0.8 + 0.2 * sin(uTime * 2.5);
            
            if (uIsPoint) {
                // Circular soft glow for points
                vec2 coord = gl_PointCoord - vec2(0.5);
                float dist = length(coord);
                
                // Soft edge: 0.0 at center, fading out to 0.5 radius
                // 1.0 - (dist * 2.0) gives linear falloff from center 
                float alpha = 1.0 - smoothstep(0.3, 0.5, dist);
                
                if (dist > 0.5) discard;
                
                baseColor.a *= alpha;
                // Make center white-ish (hot)
                baseColor.rgb += (1.0 - dist * 2.0) * 0.5;
            } else {
                // Lines
                // Simple intensity pulse
            }
            
            baseColor.rgb *= pulse;
            fragColor = baseColor;
        }
    """.trimIndent()
}
