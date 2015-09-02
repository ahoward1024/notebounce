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

	public final static float PIXELS2METERS = 100.0f; // Yay globals!

	int ScreenWidth  = 0;
	int ScreenHeight = 0;

	Box2DDebugRenderer box2DDebugRenderer;

	OrthographicCamera camera; // Orthographic because 2D

	BitmapFont debugMessage;

	SpriteBatch batch;
	Gun gun;
	Ball ball;
    Box bluebox;
    Box greenbox;
    Box yellowbox;
    Box goal;

	static World world; // Static so we can pass it easily

	String inputDebug = "Input debug: ";
	String fpsDebug = "FPS: ";

	float deltaTime = 0.0f;
    float goalTextTimer = 0.0f;
    static boolean goalHit = false;

	Matrix4 debugMatrix; // For Box2D's debug drawing projection

	boolean drawBall = false;
	boolean ballCreated = false;

	// Yes it is not capitalized. Come fight me, bro.
	public notebounce(int width, int height) {
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

		gun = new Gun(2, 2, 0.8f); // TODO(alex): better size for gun?

		// Because the world's timestep will be 1/300, we need to make gravity
		// _a lot_ more than the standard 9.8 or 10. Otherwise the ball will act
		// like it is in space after it slows down quite a bit.
		// 100 gives a good balance.
		world = new World(new Vector2(0, -200.0f), true);
        world.setContactListener(new Contacts());

		// Build the lines for the bouding box that makes it so the ball
		// does not go off the screen
		createLine(0.0f, 0.0f, ScreenWidth / PIXELS2METERS, 0.0f); // BOTTOM
		createLine(0.0f, 0.0f, 0.0f, ScreenHeight / PIXELS2METERS); //RIGHT
		createLine((ScreenWidth / PIXELS2METERS) - 0.0f, 0.0f, // LEFT
				(ScreenWidth / PIXELS2METERS) - 0.0f, ScreenHeight / PIXELS2METERS);
		createLine(0.0f, (ScreenHeight / PIXELS2METERS) - 0.0f, // TOP
                ScreenWidth / PIXELS2METERS, (ScreenHeight / PIXELS2METERS) - 0.0f);

        // Create some boxes for the ball to interact with
        bluebox = new Box(ScreenWidth/2.0f, ScreenHeight/2.0f - 300, 2.5f, Box.BoxType.blue);
        greenbox = new Box(ScreenWidth - 50.0f, 220.0f, Box.BoxType.green);
        yellowbox = new Box(550.0f, ScreenHeight - 50.0f, Box.BoxType.yellow);
        goal = new Box(ScreenWidth, 0.0f, Box.BoxType.goal);
	}

	@Override
	public void render () {
		// OpenGL
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();

		// Grab mouse input
		Vector2 mouse = new Vector2(Gdx.input.getX(), Gdx.input.getY());
		boolean click = Gdx.input.isButtonPressed(Input.Buttons.LEFT);

		// We need the mouse's Y to be normalized because LibGDX
		// defines the graphic's (0,0) to be at the _bottom_ left corner
		// while the input's (0,0) is at the _top_ left
		float graphicsY = ScreenHeight - mouse.y;

		// Find the angle for the gun and ball's projection arc based on where the mouse
		// is located on the screen. Works best if the gun's texture is defaulted to point
		// towards the right.
		float angle = (float)Math.atan2(graphicsY - gun.getCenterX(), mouse.x - gun.getCenterY());
		angle *= (180/Math.PI);

		// Reset the angle if it goes negative
		if(angle < 0) {
			angle = 360 - (-angle);
		}
		// Update the input debug string to hold input
		inputDebug = "inX: " + mouse.x +
				     " | inY: " + mouse.y +
				     " (" + graphicsY + ")" + " | Angle: " + String.format("%.2f", angle) +
				" | Click: " + click;

		camera.update(); // Update the camera just before drawing
		batch.begin();  // Start the batch drawing

		// Draw the ball only if it has been shot
		if(drawBall) {
			// We have to set ALL of the ball's sprite's parameters because we are
			// using the batch to draw it, not drawing it in the batch.
			batch.draw(ball.sprite(),
					ball.sprite().getX(), ball.sprite().getY(),
					ball.sprite().getOriginX(), ball.sprite().getOriginY(),
					ball.sprite().getWidth(), ball.sprite().getHeight(),
					ball.sprite().getScaleX(), ball.sprite().getScaleY(),
					ball.sprite().getRotation());
		}
		// Now draw the gun so it is over the ball
		gun.sprite().setRotation(angle);
		gun.sprite().draw(batch);
        // Draw environment pieces
        // TODO(alex): These need to be in an array!!!
        bluebox.sprite().draw(batch);
        greenbox.sprite().draw(batch);
        yellowbox.sprite().draw(batch);
        goal.sprite().draw(batch);

        // Draw debug inputs last so they are always on top
        if(goalHit) {
            debugMessage.setColor(Color.RED);
            debugMessage.draw(batch, "GOAL!", ScreenWidth/2, ScreenHeight/2);
            if(goalTextTimer > 2.0f) {
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

		world.step(1.0f / 300.0f, 6, 2); // 1/300 is great! Everything else is terrible... (no 1/60)
		// The copy the camera's projection and scale it to the size of the Box2D world
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);
		box2DDebugRenderer.render(world, debugMatrix); // Render the Box2D debug shapes

		// Create a ball if the mouse has been clicked and there is not already a ball in the world
		if(click && !ballCreated) {
			ball = new Ball(gun.getCenterX(), gun.getCenterY(), 0.3f);
			drawBall = true;
			ballCreated = true;
			float mXDir = (float)Math.cos(angle * Math.PI / 180);
			float mYDir = (float)Math.sin(angle * Math.PI / 180);
			float power = 20;
			Vector2 impulse = new Vector2(mXDir * power / 8, mYDir * power / 8);
			ball.body().applyLinearImpulse(impulse, ball.body().getWorldCenter(), true);
		}

		// Set the ball's sprite position the the same position as the ball's Box2D body position
		if(ballCreated) {
			ball.sprite().setPosition((ball.body().getPosition().x * PIXELS2METERS)
							- ball.sprite().getOriginX(),
					(ball.body().getPosition().y * PIXELS2METERS)
							- ball.sprite().getOriginY());
		}

		// Destroy the current ball in the world (if there is one) so another can be shot
		if(Gdx.input.isKeyJustPressed(Input.Keys.F) && ballCreated) {
			drawBall = false;
			ball.sprite().getTexture().dispose();
			ball.body().destroyFixture(ball.body().getFixtureList().first());
			ball = null;
			ballCreated = false;
		}
	}

	// We need to pass the world to any Box2D object that needs to be created
	// Doing so statically I think is the easiest...
	public static World getWorld() {
		return world;
	}
    public static void hitGoal() {
        goalHit = true;
    }
}
