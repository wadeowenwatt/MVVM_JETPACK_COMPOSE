#version 300 es
precision mediump float;

in vec4 vColor;
in float vBrightness;
uniform float uTime;
uniform bool uIsPoint;
uniform vec4 uColor; // New uniform for dynamic color

out vec4 fragColor;

void main() {
    // Use uColor if provided (alpha > 0), otherwise fallback to vColor or default
    vec4 baseColor = (uColor.a > 0.0) ? uColor : vColor;

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
