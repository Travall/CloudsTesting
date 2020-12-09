package com.travall.clouds;

import static com.badlogic.gdx.Gdx.gl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.travall.clouds.tools.Clouds;

public class Main extends ApplicationAdapter {
	public PerspectiveCamera camera;
	public CameraInputController cameraController;
	public Clouds clouds;

	@Override
	public void create() {
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.1f;
		camera.far = 500f;
		camera.translate(new Vector3(Clouds.CLOUD_ROW / 4, 0, Clouds.CLOUD_ROW / 4));

		clouds = new Clouds();

		cameraController = new CameraInputController(camera);
		cameraController.target = camera.position.cpy();
		Gdx.input.setInputProcessor(cameraController);
	}

	@Override
	public void render() {
		//gl.glViewport(0, 0, 256, 256);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//cameraController.update();
		//camera.update();
		
		clouds.render(camera);
	}

	@Override
	public void dispose() {
		clouds.dispose();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}
}