#version 100
attribute vec4 a_position;

uniform mat4 u_projTrans;
uniform vec3 u_trans;

in vec3 i_offset;

void main() {
	gl_Position = u_projTrans * ((a_position + vec4(i_offset,1.0)) + vec4(u_trans, 1.0));
}