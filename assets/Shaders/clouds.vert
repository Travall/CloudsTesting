#version 310 es
#ifdef GL_ES
    precision highp float;
#endif

in vec3 a_position;

in vec2 offset;

uniform float offsetf;
uniform float shift;
uniform float size;

uniform sampler2D noiseMap;
uniform mat4 projTrans;

void main() {
	float noise = texture(noiseMap, offset / 512.0 / offsetf).r;
	
	noise += shift;
	
	if (noise < 0.001) {
		noise = noise > 0.01 ? 0.0 : noise;
		
		noise *= size;
		
		vec3 newPos = (a_position * noise) + vec3(offset.x, 0.0, offset.y);
		gl_Position = projTrans * vec4(newPos, 1.0);
	} else {
		gl_Position = vec4(0.0);
	}
}