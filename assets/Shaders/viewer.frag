#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec2 coord;

out vec4 color;

uniform sampler2D text;

void main() {
	float a = -texture(text, coord).r;
 	color = vec4((a*0.5)+0.5, 0, 0, 1.0);
}