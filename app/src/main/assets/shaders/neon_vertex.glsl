#version 300 es
precision mediump float;

layout(location = 0) in vec4 aPosition;
layout(location = 1) in vec4 aColor;

uniform mat4 uMVPMatrix;
uniform float uTime;
uniform float uCameraDistance; 

out vec4 vColor;
out float vBrightness;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    
    // Depth-based brightness attenuation
    // Dynamic calculation based on camera distance
    float nearDist = uCameraDistance - 1.0;
    float farDist = uCameraDistance + 1.1;
    
    // smoothstep(far_edge, near_edge, value)
    float brightness = smoothstep(farDist, nearDist, gl_Position.w);
    
    // Ensure minimum brightness so back vertices aren't totally invisible
    // User requested "even fainter" -> reduced from 0.2 to 0.05
    brightness = max(0.01, brightness);
    vBrightness = brightness;
    
    vColor = vec4(aColor.rgb * brightness, aColor.a);
    
    // Massive point size to allow for large glow
    // Base 80.0 + pulsing +/- 20.0
    gl_PointSize = 80.0 + 20.0 * sin(0.5 * 3.0); 
}
