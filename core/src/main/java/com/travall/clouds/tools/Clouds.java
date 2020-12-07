package com.travall.clouds.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.VertexBufferObject;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

import java.nio.FloatBuffer;

import static com.badlogic.gdx.Gdx.files;
import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.graphics.GL20.*;

public class Clouds implements Disposable
{

	private ShaderProgram shader;
	private Mesh sphere;
	private final PerspectiveCamera cloudCamera = new PerspectiveCamera();
	private Vector3 position = new Vector3(0,0,0);

	private final static int CLOUD_ROW = 10;
	private final static int CLOUD_COUNT = CLOUD_ROW * CLOUD_ROW;

	public Clouds() {
		this.shader = new ShaderProgram(files.internal("Shaders/clouds.vert"), files.internal("Shaders/clouds.frag"));
		shader.bind();

		MeshBuilder build = new MeshBuilder();
		build.begin(VertexAttributes.Usage.Position, GL20.GL_TRIANGLES);
		SphereShapeBuilder.build(build, 1f, 1f, 1f, 8, 8);
		sphere = build.end();

		sphere.enableInstancedRendering(true, CLOUD_COUNT, new VertexAttribute(VertexAttributes.Usage.Position, 3, "i_offset"));

		FloatBuffer offsets = BufferUtils.newFloatBuffer(CLOUD_COUNT * 3);
		for (int x = 1; x <= CLOUD_ROW; x++) {
			for (int y = 1; y <= CLOUD_ROW; y++) {
				offsets.put(new float[] {
						x,0,y});
			}
		}
		offsets.position(0);
		sphere.setInstanceData(offsets);
//		mesh.disableInstancedRendering();
	}

	public void render(PerspectiveCamera camera) {
//
		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL30.GL_BLEND);
		intsSkyCam(camera);
		shader.bind();
		shader.setUniformMatrix("u_projTrans", cloudCamera.combined);
		shader.setUniformf("u_trans", camera.position);

		sphere.render(shader, GL20.GL_TRIANGLES);


	}

	private void intsSkyCam(PerspectiveCamera camera) {
		cloudCamera.direction.set(camera.direction);
		cloudCamera.up.set(camera.up);
		cloudCamera.fieldOfView = camera.fieldOfView;
		cloudCamera.viewportWidth = camera.viewportWidth;
		cloudCamera.viewportHeight = camera.viewportHeight;
		cloudCamera.update(false);
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
