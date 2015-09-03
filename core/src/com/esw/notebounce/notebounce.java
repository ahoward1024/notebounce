package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class NoteBounce extends ApplicationAdapter implements ContactListener {

	public final static float PIXELS2METERS = 100.0f; // Yay globals!

	int ScreenWidth  = 0;
	int ScreenHeight = 0;

	Box2DDebugRenderer box2DDebugRenderer;

	OrthographicCamera camera; // Orthographic because 2D

	BitmapFont debugMessage;

	SpriteBatch batch;
	Gun gun;
	Ball ball;

    Sound goalNoise;
    boolean goalNoisePlaying = false;
    boolean goalHit = false;
    boolean goalWasHit = false;
    float goalTextTimer = 0.0f;

	Sound[] notes = new Sound[8];
	int notePtr = 0;
	float timeSinceLastBlueNote = 0.0f;
	float timeSinceLastGreenNote = 0.0f;
	float timeSinceLastYellowNote = 0.0f;
	boolean playNotes = true;

	static World world; // Static so we can pass it easily

	String inputDebug = "Input debug: ";
	String fpsDebug = "FPS: ";

	float deltaTime = 0.0f;

	Matrix4 debugMatrix; // For Box2D's debug drawing projection

	boolean drawBall = false;
	boolean ballShot = false;

	TmxMapLoader mapLoader;
	TiledMap map;
	OrthogonalTiledMapRenderer renderer;

	// Yes it is not capitalized. Come fight me, bro.
	public NoteBounce(int width, int height) {
		ScreenWidth  = width;
		ScreenHeight = height;
	}

	// This is called to create the Edge Lines for the boundaries of the screen
	// so the ball will stay within the screen's bounds
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
		ground.createFixture(groundFixtureDef).setUserData("boundary");
		edgeShape.dispose();
	}
	
	@Override
	public void create () {
		Box2D.init(); // MUST initialize Box2D before using it!
		box2DDebugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera(ScreenWidth, ScreenHeight);
		camera.position.set(ScreenWidth / 2, ScreenHeight / 2, 0.0f);
		camera.update();

		batch = new SpriteBatch();
		// Set the projection matrix to combined to the camera's
		// combined projection and matrix
		batch.setProjectionMatrix(camera.combined);

		debugMessage = new BitmapFont();

		// Because the world's timestep will be 1/300, we need to make gravity
		// _a lot_ more than the standard 9.8 or 10. Otherwise the ball will act
		// like it is in space after it slows down quite a bit.
		// 100 gives a good balance.
		world = new World(new Vector2(0, -200.0f), true);
		world.setContactListener(this);

		gun = new Gun(20, 20, 0.8f); // TODO(alex): better size for gun?
		ball = new Ball(gun.getCenterX(), gun.getCenterY());

		// Build the lines for the bouding box that makes it so the ball
		// does not go off the screen
		createLine(0.0f, 0.0f, ScreenWidth / PIXELS2METERS, 0.0f); // BOTTOM
		createLine(0.0f, 0.0f, 0.0f, ScreenHeight / PIXELS2METERS); //RIGHT
		createLine((ScreenWidth / PIXELS2METERS) - 0.0f, 0.0f, // LEFT
				(ScreenWidth / PIXELS2METERS) - 0.0f, ScreenHeight / PIXELS2METERS);
		createLine(0.0f, (ScreenHeight / PIXELS2METERS) - 0.0f, // TOP
                ScreenWidth / PIXELS2METERS, (ScreenHeight / PIXELS2METERS) - 0.0f);

        goalNoise = Gdx.audio.newSound(Gdx.files.internal("goal.mp3"));

		notes[0] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C4.mp3"));
		notes[1] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/D4.mp3"));
		notes[2] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/E4.mp3"));
		notes[3] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/F4.mp3"));
		notes[4] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/G4.mp3"));
		notes[5] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/A4.mp3"));
		notes[6] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/B4.mp3"));
		notes[7] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C5.mp3"));

		notePtr = 0;

		mapLoader = new TmxMapLoader();
		map = mapLoader.load("tmx/level0.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);

		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		Body body;

		for(int i = 1; i < map.getLayers().getCount(); i++) {
			for (MapObject object : map.getLayers().get(i).getObjects().getByType(RectangleMapObject.class)) {
				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				bodyDef.type = BodyDef.BodyType.StaticBody;
				bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / PIXELS2METERS,
						(rect.getY() + rect.getWidth() / 2) / PIXELS2METERS);
				body = world.createBody(bodyDef);
				shape.setAsBox((rect.getWidth() / 2) / PIXELS2METERS, (rect.getHeight() / 2) / PIXELS2METERS);
				fixtureDef.shape = shape;
				body.createFixture(fixtureDef).setUserData(map.getLayers().get(i).getName());
			}
		}

		shape.dispose();
	}

	@Override
	public void render () {

		renderer.setView(camera);
		// OpenGL
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();
		timeSinceLastBlueNote += deltaTime;
		timeSinceLastGreenNote += deltaTime;
		timeSinceLastYellowNote += deltaTime;

		renderer.render();

		// Grab mouse input
		Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		boolean lclick = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

		// We need the mouse's Y to be normalized because LibGDX
		// defines the graphic's (0,0) to be at the _bottom_ left corner
		// while the input's (0,0) is at the _top_ left
		float mouseGraphicsY = ScreenHeight - mouse.y;

		// Find the angle for the gun and ball's projection arc based on where the mouse
		// is located on the screen. Works best if the gun's texture is defaulted to point
		// towards the right.
		float angle = (float)Math.atan2(mouseGraphicsY - gun.getCenterY(), mouse.x - gun.getCenterX());
		angle *= (180/Math.PI);

		// Reset the angle if it goes negative
		if(angle < 0) {
			angle = 360 - (-angle);
		}
		// Update the input debug string to hold input
		inputDebug = "inX: " + mouse.x +
				     " | inY: " + mouse.y +
				     " (" + mouseGraphicsY + ")" + " | Angle: " + String.format("%.2f", angle) +
				" | Click: " + lclick;

		camera.update(); // Update the camera just before drawing
		batch.begin();  // Start the batch drawing

		// We have to set ALL of the ball's sprite's parameters because we are
		// using the batch to draw it, not drawing it in the batch.
		batch.draw(ball.sprite(),
				   ball.sprite().getX(), ball.sprite().getY(),
				   ball.sprite().getOriginX(), ball.sprite().getOriginY(),
				   ball.sprite().getWidth(), ball.sprite().getHeight(),
				   ball.sprite().getScaleX(), ball.sprite().getScaleY(),
				   ball.sprite().getRotation());

		// Now draw the gun so it is over the ball
		gun.sprite().setRotation(angle);
		gun.sprite().draw(batch);

        // Draw debug inputs last so they are always on top
        if(goalHit) {
            goalWasHit = true;
            debugMessage.setColor(Color.RED);
            debugMessage.draw(batch, "GOAL!", ScreenWidth/2, ScreenHeight/2);
            if(goalTextTimer > 10.0f) { // Keep the text up for 10 seconds
                goalHit = false;
                goalTextTimer = 0.0f;
            }
            goalTextTimer += deltaTime;
        }
        debugMessage.setColor(Color.GREEN);
        debugMessage.draw(batch, inputDebug, 10, ScreenHeight - 10);
        debugMessage.setColor(Color.YELLOW);
        debugMessage.draw(batch, fpsDebug + Gdx.graphics.getFramesPerSecond(),
				          ScreenWidth - 60, ScreenHeight - 10);

		batch.end(); // Stop the batch drawing

        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) world.step(1.0f / 3000.0f, 6, 2);
        else if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) world.step(1.0f / 100.0f, 6, 2);
        else world.step(1.0f / 300.0f, 6, 2); // 1/300 is great! Everything else is terrible... (no 1/60)
		// The copy the camera's projection and scale it to the size of the Box2D world
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);
		box2DDebugRenderer.render(world, debugMatrix); // Render the Box2D debug shapes

		// Create a ball if the mouse has been clicked and there is not already a ball in the world
		if(lclick && !ballShot) {
			drawBall = true;
			ballShot = true;
			ball.body().setType(BodyDef.BodyType.DynamicBody); // Set the ball to dynamic so it moves
			float mXDir = (float)Math.cos(angle * Math.PI / 180);
			float mYDir = (float)Math.sin(angle * Math.PI / 180);
			float power = 23;
			Vector2 impulse = new Vector2(mXDir * power / 8, mYDir * power / 8);
			ball.body().applyLinearImpulse(impulse, ball.body().getWorldCenter(), true);
		}

		// Set the ball's sprite position the the same position as the ball's Box2D body position
		if(ballShot) {
			ball.sprite().setPosition((ball.body().getPosition().x * PIXELS2METERS) -
							           ball.sprite().getOriginX(),
					                  (ball.body().getPosition().y * PIXELS2METERS) -
							           ball.sprite().getOriginY());
		}

		// Destroy the current ball in the world (if there is one) so another can be shot
        // Stop any sound (if it was playing)
        // This essentially "resets" the level
		if(Gdx.input.isKeyJustPressed(Input.Keys.F) && ballShot) {
			ball.sprite().getTexture().dispose();
			ball.body().destroyFixture(ball.body().getFixtureList().first());
			ballShot = false;
			ball = new Ball(gun.getCenterX(), gun.getCenterY());
			if(goalWasHit) {
                if(goalNoisePlaying) goalNoise.stop();
                goalNoisePlaying = false;
                goalWasHit = false;
				playNotes = true;
            }
		}
	}

	// We need to pass the world to any Box2D object that needs to be created
	// Doing so statically I think is the easiest...
	public static World getWorld() {
		return world;
	}

	boolean flipsies = true;

	// Implemented from ContactListener
	// Handles all the collision detection
	public void beginContact(Contact c) {
		Fixture fa = c.getFixtureA(); // Usually a static object
		Fixture fb = c.getFixtureB(); // Usually a dynamic object

		// Test if goal was hit
		if(fa.getUserData().equals("goal")) {
			goalHit = true;
			// Play the goal noise if it was not already playing
			if(!goalNoisePlaying) {
				goalNoise.play();
				goalNoisePlaying = true;
				playNotes = false;
			}
		}

		if(playNotes) {
			if (fa.getUserData().equals("blue") && timeSinceLastBlueNote > 0.2f) {
				if (notePtr == notes.length - 1) notePtr = 0;
				else notePtr++;
				notes[notePtr].play();
				timeSinceLastBlueNote = 0.0f;
			}

			if (fa.getUserData().equals("green") && timeSinceLastGreenNote > 0.2f) {
				if (notePtr == 0) notePtr = notes.length - 1;
				else notePtr--;
				notes[notePtr].play();
				timeSinceLastGreenNote = 0.0f;
			}

			if (fa.getUserData().equals("yellow") && timeSinceLastYellowNote > 0.2f) {
				if (flipsies) {
					flipsies = !flipsies;
					notePtr += 4;
					if (notePtr > notes.length - 1) {
						int i = notePtr - (notes.length - 1);
						notePtr = 0;
						notePtr += i;
					}
					notes[notePtr].play();
				} else {
					flipsies = !flipsies;
					notePtr -= 4;
					if (notePtr < 0) {
						notePtr = Math.abs(notePtr);
					}
					notes[notePtr].play();
				}
				timeSinceLastYellowNote = 0.0f;
			}
		}
	}

	public void endContact(Contact c) {

	}

	public void preSolve(Contact c, Manifold m) {

	}

	public void postSolve(Contact c, ContactImpulse ci) {

	}
}
