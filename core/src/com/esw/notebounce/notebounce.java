package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class notebounce extends ApplicationAdapter {

	final float PIXELS2METERS = 100.0f;

	int ScreenWidth  = 0;
	int ScreenHeight = 0;

	Box2DDebugRenderer box2DDebugRenderer;

	OrthographicCamera camera;

	BitmapFont debugMessage;

	SpriteBatch batch;
	Sprite gun;
	Sprite ball;

	World world;
	Body ballBody;

	String inputDebug = "Input debug: ";
	String fpsDebug = "FPS: ";

	float deltaTime = 0.0f;
	float debugClearClock = 0.0f;

	Matrix4 debugMatrix;

	public notebounce(int width, int height) {
		ScreenWidth  = width;
		ScreenHeight = height;
	}

	public void createLine(float x1, float y1, float x2, float y2) {
		Body ground;
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyDef.BodyType.StaticBody;
		groundBodyDef.position.set(0.0f, 0.0f);
		FixtureDef groundFixtureDef = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(x1, y1, x2, y2);
		groundFixtureDef.shape = edgeShape;
		ground = world.createBody(groundBodyDef);
		ground.createFixture(groundFixtureDef);
		edgeShape.dispose();
	}
	
	@Override
	public void create () {
		Box2D.init();
		box2DDebugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera(ScreenWidth, ScreenHeight);
		camera.position.set(ScreenWidth / 2, ScreenHeight / 2, 0.0f);
		camera.update();

		batch = new SpriteBatch();

		debugMessage = new BitmapFont();

		gun = new Sprite(new Texture("gun.png"));
		gun.setCenter(ScreenWidth / 2, ScreenHeight / 2);
		gun.setOriginCenter();
		gun.setScale(0.8f);

		ball = new Sprite(new Texture("ball.png"));
		ball.setCenter(ScreenWidth / 2, ScreenHeight / 2);
		ball.setOriginCenter();
		ball.setScale(0.3f);

		world = new World(new Vector2(0, -5f), true);

		BodyDef ballBodyDef = new BodyDef();
		ballBodyDef.type = BodyDef.BodyType.DynamicBody;
		ballBodyDef.position.set((ball.getX() + ball.getOriginX()) / PIXELS2METERS,
				                 (ball.getY() + ball.getOriginY()) / PIXELS2METERS);
		ballBody = world.createBody(ballBodyDef);
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(((ball.getWidth() * 0.3f)/2) / PIXELS2METERS);
		FixtureDef ballFixtureDef = new FixtureDef();
		ballFixtureDef.shape = circleShape;
		ballFixtureDef.density = 0.5f;
		ballFixtureDef.friction = 0.4f;
		ballFixtureDef.restitution = 0.9f; // Make it bounce a little bit
		ballBody.createFixture(ballFixtureDef);
		circleShape.dispose();

		createLine(0.0f, 0.1f, ScreenWidth/PIXELS2METERS, 0.1f); // BOTTOM
		createLine(0.1f, 0.0f, 0.1f, ScreenHeight/PIXELS2METERS); //RIGHT
		createLine((ScreenWidth/PIXELS2METERS) - 0.1f, 0.0f, // LEFT
				   (ScreenWidth/PIXELS2METERS) - 0.1f, ScreenHeight/PIXELS2METERS);
		createLine(0.0f, (ScreenHeight/PIXELS2METERS) - 0.1f, // TOP
				   ScreenWidth/PIXELS2METERS, (ScreenHeight/PIXELS2METERS) - 0.1f);

	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();
		debugClearClock += deltaTime;

		if(debugClearClock > 1) {
			//inputDebug = "Input debug: ";
			debugClearClock = 0.0f;
		}

		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);

		Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		boolean click = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

		float graphicsY = ScreenHeight - mouse.y;

		// TODO(alex): We need to set the rotation point relative to the gun's center
		// todo relative to the screen coordinates. Can't be ScreenHeight/2, ScreenWidth/2
		float angle = (float)Math.atan2(graphicsY - ScreenHeight/2, mouse.x - ScreenWidth/2);
		angle *= (180/Math.PI);

		if(angle < 0) {
			angle = 360 - (-angle);
		}
		inputDebug = "inX: " + mouse.x +
				     " | inY: " + mouse.y +
				     " (" + graphicsY + ")" + " | Angle: " + String.format("%.2f", angle) +
				     " | Click: " + click;

		ball.setPosition((ballBody.getPosition().x * PIXELS2METERS) - ball.getOriginX(),
				(ballBody.getPosition().y * PIXELS2METERS) - ball.getOriginY());

		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		debugMessage.setColor(Color.GREEN);
		debugMessage.draw(batch, inputDebug, 10, 30);
		debugMessage.setColor(Color.YELLOW);
		debugMessage.draw(batch, fpsDebug + Gdx.graphics.getFramesPerSecond(), ScreenWidth - 60, 30);

		gun.setRotation(angle);
		gun.draw(batch);
		batch.draw(ball,
				   ball.getX(),       ball.getY(),
				   ball.getOriginX(), ball.getOriginY(),
				   ball.getWidth(),   ball.getHeight(),
				   ball.getScaleX(),  ball.getScaleY(),
				   ball.getRotation());
		batch.end();

		world.step(Gdx.graphics.getDeltaTime(), 6, 2);
		box2DDebugRenderer.render(world, debugMatrix);
	}
}
