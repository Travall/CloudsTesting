#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec4 a_position;

out vec2 coord;

void main () {
	coord = ((a_position.xy * 0.5) + 1.0) * 0.5;
	gl_Position = a_position;
}