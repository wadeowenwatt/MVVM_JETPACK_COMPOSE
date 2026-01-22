#version 300 es
precision mediump float;
// Legacy shader, use WITH_TOGGLE instead
in vec4 vColor;
out vec4 fragColor;
void main() { fragColor = vColor; } 
