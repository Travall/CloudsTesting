package com.travall.clouds.tools;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

import java.nio.FloatBuffer;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;

public class Clouds implements Disposable
{
	public final static int CLOUD_ROW = 512;
	
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;
	private final static float OFFSET = 0.5f;
	private final static float SHIFT = 0.1f;
	private final static float SIZE = 2.0f;

	private final ShaderProgram shader;
	private final Mesh sphere;
	private final NoiseGPU gpu;

	private float offsetX = 0;
	private float offsetY = 0;

	public Clouds() {

		shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();

		final MeshBuilder build = new MeshBuilder();
		build.begin(Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 3f, 3f, 3f, 8, 6);
		sphere = build.end();
		
		gpu = new NoiseGPU();

		sphere.enableInstancedRendering(true, CLOUD_COUNT, new VertexAttribute(VertexAttributes.Usage.Position, 2, "offset"));
	}

	FloatBuffer offsets = BufferUtils.newFloatBuffer(CLOUD_COUNT * 2);
	float[] temp = new float[2];
	
	public void render(PerspectiveCamera camera) {
		
		gpu.noise(OFFSET);
		offsets.clear();
		for (int x = 0; x < CLOUD_ROW; x++) {
			for (int y = 0; y < CLOUD_ROW; y++) {
				temp[0] = x * OFFSET;
				temp[1] = y * OFFSET;
				offsets.put(temp);
			}
		}
		offsets.flip();
		sphere.setInstanceData(offsets);
		
		gpu.texture.bind();
		gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_BLEND);
		shader.bind();
		shader.setUniformMatrix("projTrans", camera.combined);
		shader.setUniformf("offsetf", OFFSET);
		shader.setUniformf("shift", SHIFT);
		shader.setUniformf("size", SIZE);

		gl.glEnable(GL20.GL_CULL_FACE);
		gl.glDepthMask(false);
		sphere.render(shader, GL20.GL_TRIANGLES);
		gl.glDepthMask(true);
		gl.glDisable(GL20.GL_CULL_FACE);

		offsetX += 0.02f;
		offsetY += 0.02f;
	}

	@Override
	public void dispose() {
		shader.dispose();
		sphere.dispose();
		gpu.dispose();
	}
}
