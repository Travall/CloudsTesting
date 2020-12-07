package com.travall.clouds.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.travall.clouds.noise.FastNoiseOctaves;
import com.travall.clouds.noise.OpenSimplexOctaves;

import java.nio.FloatBuffer;
import java.util.Random;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class Clouds implements Disposable
{

	private ShaderProgram shader;
	private Mesh sphere;

	public final static int CLOUD_ROW = 200;
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;
	FastNoiseOctaves noise = new FastNoiseOctaves(4,0.5,new Random());

	float offsetX = 0;
	float offsetY = 0;
	float offsetTime = 0;

	public Clouds() {

		this.shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();

		MeshBuilder build = new MeshBuilder();
		build.begin(VertexAttributes.Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 3f, 3f, 3f, 24, 16);
		sphere = build.end();

		sphere.enableInstancedRendering(true, CLOUD_COUNT, new VertexAttribute(VertexAttributes.Usage.Position, 2, "i_offset"),new VertexAttribute(VertexAttributes.Usage.Generic, 1, "i_scale"));
//		mesh.disableInstancedRendering();
	}

	FloatBuffer offsets = BufferUtils.newFloatBuffer(CLOUD_COUNT * 3);
	float[] temp = new float[3];
	
	public void render(PerspectiveCamera camera) {

		offsets.clear();
		
		for (int x = 1; x <= CLOUD_ROW; x++) {
			for (int y = 1; y <= CLOUD_ROW; y++) {
				float height = (noise.getNoise((x * 0.3f) + offsetX, (y * 0.3f) + offsetY, offsetTime)+0.03f) * 2.5f;
				if(height < 0.01) height = 0;
				
				temp[0] = x;
				temp[1] = y;
				temp[2] = height;
				
				offsets.put(temp);
			}
		}

		offsets.flip();
		sphere.setInstanceData(offsets);
//
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL30.GL_BLEND);
		shader.bind();
		shader.setUniformMatrix("u_projTrans", camera.combined);

		sphere.render(shader, GL20.GL_TRIANGLES);

		offsetX += 0.02f;
		offsetY += 0.02f;
		offsetTime += 0.02f;
	}

	@SuppressWarnings("unused")
	private void reload() {
		shader.dispose();
		shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();
		Gdx.gl.glUseProgram(0);
	}

	@Override
	public void dispose() {
		shader.dispose();
	}
}
