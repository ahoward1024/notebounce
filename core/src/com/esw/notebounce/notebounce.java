package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class NoteBounce extends ApplicationAdapter implements InputProcessor {

	public final static float PIXELS2METERS = 100.0f; // Yay globals!

	int ScreenWidth  = 0;
	int ScreenHeight = 0;

	OrthographicCamera camera; // Orthographic because 2D

	Box2DDebugRenderer box2DDebugRenderer;
	BitmapFont debugMessage;
	Rectangle gunDebugRectangle;
	ShapeRenderer debugShapeRenderer;

	SpriteBatch batch;
	Gun gun;
	static Ball ball;
	static Sprite ripple;
	Sprite crosshair;

	CollisionDetection collisionDetector;

    static Sound goalNoise;

    boolean goalWasHit = false;
    float goalTextTimer = 0.0f;

	float timestep = 300.0f;

	static World world; // Static so we can pass it easily
	static boolean playNotes = true;
	static boolean goalHit = false;
	static boolean goalNoisePlaying = false;
	static boolean playRipple = false;
	static Sound[] notes = new Sound[8];
	static int notePtr = 0;

	String inputDebug = "";        // DEBUG
	String mouseClickDebug = "";   // DEBUG
	String ballPositionDebug = ""; // DEBUG
	String gunPositionDebug = "";  // DEBUG
	String fpsDebug = "FPS: ";     // DEBUG

	float deltaTime = 0.0f;

	Matrix4 debugMatrix; // For Box2D's debug drawing projection

	boolean ballShot = false;

	TmxMapLoader mapLoader;
	TiledMap map[] = new TiledMap[5];
	OrthogonalTiledMapRenderer mapRenderer;
	int levelPtr = 0;

	float angle = 0.0f;
	boolean shoot = false;
	Vector2 mouseClick = new Vector2(0, 0);
	Vector2 mouseUnClick = new Vector2(0, 0);
	boolean moveBall = false;

	Vector2 mouse = new Vector2(0,0);
	float lastUsedAngle = 45.0f;
	float lastUsedPower = 12.5f;

	float power = 0.0f;

	boolean showGoalHit= false;

	boolean touch = false;
	boolean reset = false;

	/**
	 * Create a new NoteBounce level and set the ScreenWidth and ScreenHeight.
	 * @param width The width of the screen.
	 * @param height The height of the screen.
	 */
	public NoteBounce(int width, int height) {
		ScreenWidth  = width;
		ScreenHeight = height;
	}

	/**
	 *  This is called to create the Edge Lines for the boundaries of the screen
	 *  so the ball will stay within the screen's bounds. If the edge is supposed to be
	 *  for the bottom of the screen the bottom value should be set to true. This simplifies
	 *  some of the collision detection code for later.
	 * @param x1 The beginning x coordinate.
	 * @param y1 The beginning y coordinate.
	 * @param x2 The ending x coordinate.
	 * @param y2 The ending y coordinate.
	 * @param bottom Indicates if this edge is for the bottom edge of the screen when true.
	 */
	public void createLine(float x1, float y1, float x2, float y2, boolean bottom) {
		Body ground;
		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.type = BodyDef.BodyType.StaticBody;
		groundBodyDef.position.set(0.0f, 0.0f);
		FixtureDef groundFixtureDef = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(x1, y1, x2, y2);
		groundFixtureDef.shape = edgeShape;
		ground = world.createBody(groundBodyDef);
		if(bottom) ground.createFixture(groundFixtureDef).setUserData("boundaryBot");
		else ground.createFixture(groundFixtureDef).setUserData("boundary");
		edgeShape.dispose();
	}

	final float gravity = -200.0f;
	/**
	 * Creates the game world.
	 */
	@Override
	public void create() {
		Box2D.init(); // MUST initialize Box2D before using it!
		box2DDebugRenderer = new Box2DDebugRenderer();
		debugShapeRenderer = new ShapeRenderer();
		debugShapeRenderer.setAutoShapeType(true);

		camera = new OrthographicCamera(ScreenWidth, ScreenHeight);
		camera.position.set(ScreenWidth / 2, ScreenHeight / 2, 0.0f);
		camera.update();

		batch = new SpriteBatch();
		// Set the projection matrix to combined to the camera's
		// combined projection and matrix
		batch.setProjectionMatrix(camera.combined);

		debugMessage = new BitmapFont();

		Gdx.input.setInputProcessor(this);

		// Because the world's timestep will be 1/300, we need to make gravity
		// _a lot_ more than the standard 9.8 or 10. Otherwise the ball will act
		// like it is in space after it slows down quite a bit. 200 gives a good balance.
		world = new World(new Vector2(0, gravity), true);
		collisionDetector = new CollisionDetection();
		world.setContactListener(collisionDetector);

		gun = new Gun(30.0f, 30.0f);
		gunDebugRectangle = gun.sprite().getBoundingRectangle();
		ball = new Ball(gun.getCenterX(), gun.getCenterY());

		// Build the lines for the bouding box that makes it so the ball
		// does not go off the screen
		createLine(0.0f, 0.0f, ScreenWidth / PIXELS2METERS, 0.0f, true); // BOTTOM
		createLine(0.0f, 0.0f, 0.0f, ScreenHeight / PIXELS2METERS, false); //RIGHT
		createLine((ScreenWidth / PIXELS2METERS) - 0.0f, 0.0f, // LEFT
				(ScreenWidth / PIXELS2METERS) - 0.0f, ScreenHeight / PIXELS2METERS, false);
		createLine(0.0f, (ScreenHeight / PIXELS2METERS) - 0.0f, // TOP
				ScreenWidth / PIXELS2METERS, (ScreenHeight / PIXELS2METERS) - 0.0f, false);

        goalNoise = Gdx.audio.newSound(Gdx.files.internal("goal.mp3"));

		// Create the bodyArray to hold of of the notes for the note blocks
		notes[0] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C4.mp3"));
		notes[1] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/D4.mp3"));
		notes[2] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/E4.mp3"));
		notes[3] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/F4.mp3"));
		notes[4] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/G4.mp3"));
		notes[5] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/A4.mp3"));
		notes[6] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/B4.mp3"));
		notes[7] = Gdx.audio.newSound(Gdx.files.internal("notes/C4/C5.mp3"));

		notePtr = 0;

		crosshair = new Sprite(new Texture(Gdx.files.internal("crosshair.png")));

		createLevelArray();
		loadLevel(0);

		System.out.println(map.length);
	}

	void createLevelArray() {
		mapLoader = new TmxMapLoader();
		int i; // DEBUG
		System.out.println("Map length: " + map.length); // DEBUG
		// Load each level in the tmx/ levels folder
		for(i = 0; i < map.length; i++) {
			System.out.println("Load: tmx/level" + i + ".tmx");
			map[i] =  mapLoader.load("tmx/level" + i + ".tmx");
		}
		System.out.println("Create Level Array: " + i); // DEBUG
	}

	void loadLevel(int level) {
		System.out.println("Load Level: " + level); // DEBUG
		mapRenderer = new OrthogonalTiledMapRenderer(map[level]); // Create a mapRenderer for the level

		// These are used to create the bodies and fixtures for all of the note blocks in the level.
		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();
		FixtureDef fixtureDef = new FixtureDef();
		Body body;

		// Go through all of the layers in the Tiled map and find all of the object layers so
		// we can make Box2D static objects out of each layer. This is defined in a specific format:
		// Layer 0 of a Tiled map will always be all of the sprites (tiles). Each subsequent layer is
		// an object layer with the same name of the corresponding block it goes around (blue, green,
		// yellow, etc.) We must create rectangles around all of the object layers and create
		// static fixtures so the ball has something to collide with.
		for(int i = 1; i < map[level].getLayers().getCount(); i++) {
			for (MapObject object :
				map[level].getLayers().get(i).getObjects().getByType(RectangleMapObject.class)) {

				Rectangle rect = ((RectangleMapObject) object).getRectangle();
				bodyDef.type = BodyDef.BodyType.StaticBody;
				bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / PIXELS2METERS,
					(rect.getY() + rect.getWidth() / 2) / PIXELS2METERS);
				body = world.createBody(bodyDef);
				shape.setAsBox((rect.getWidth() / 2) / PIXELS2METERS,
					(rect.getHeight() / 2) / PIXELS2METERS);
				fixtureDef.shape = shape;
				fixtureDef.density = 1.0f;
				fixtureDef.restitution = 0.0f;
				body.createFixture(fixtureDef).setUserData(map[level].getLayers().get(i).getName());
			}
		}

		shape.dispose(); // Make sure to dispose of the shape to free some now unused memory.
	}

	/**
	 * A simple linear interpolation function (https://en.wikipedia.org/wiki/Linear_interpolation).
	 * @param edge0 The beginning interpolation value
	 * @param edge1 The ending interpolation value
	 * @param t The timestep of interpolation
	 * @return The point on the interpolation line the number should be after the given timestep
	 */
	float lerp(float edge0, float edge1, float t) {
		return (1 - t) * edge0 + t * edge1;
	}

	/**
	 * An implementation of the smoothstep function (https://en.wikipedia.org/wiki/Smoothstep)
	 * @param edge0 The beginning interpolation value
	 * @param edge1 The ending interpolation value
	 * @param t The timestep of interpolation
	 * @return The point on the interpolation line the number should be after the given timestep
	 */
	@SuppressWarnings("unused")
	float smoothstep(float edge0, float edge1, float t)
	{
		// Scale, bias and saturate x to 0..1 range
		t = MathUtils.clamp((t - edge0)/(edge1 - edge0), 0.0f, 1.0f);
		// Evaluate polynomial
		return t * t * (3 - (2 * t));
	}

	/**
	 * An implementation of the smoothstep function (https://en.wikipedia.org/wiki/Smoothstep#Variations)
	 * @param edge0 The beginning interpolation value
	 * @param edge1 The ending interpolation value
	 * @param t The timestep of interpolation
	 * @return The point on the interpolation line the number should be after the given timestep
	 */
	@SuppressWarnings("unused")
	float smootherstep(float edge0, float edge1, float t)
	{
		// Scale, and clamp x to 0..1 range
		t = MathUtils.clamp((t - edge0)/(edge1 - edge0), 0.0f, 1.0f);
		// Evaluate polynomial
		return t * t * t * (t * ((t * 6) - 15) + 10);
	}

	final float MAX_POWER = 2.3f; //!!! move

	/**
	 * Update all of the variables needed to simulate physics
	 */
	public void updatePhysics() {
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) timestep = 3000.0f;
		else if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) timestep = 100.0f;
		else timestep = 300.0f;

		if(!sim) {
			world.step(1.0f / timestep, 6, 2);
		}

		world.clearForces();
		// Copy the camera's projection and scale it to the size of the Box2D world
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);
		box2DDebugRenderer.render(world, debugMatrix); // Render the Box2D debug shapes
	}

	/**
	 * Run a physics simulation that calculates where the ball would go if it were to be shot
	 * with the current power and angle.
	 */
	void simulate() {
		Ball b = new Ball(gun.getCenterX(), gun.getCenterY());
		b.body().getFixtureList().first().setUserData("null");
		b.body().setType(BodyDef.BodyType.DynamicBody);
		b.sprite().getTexture().dispose();
		b.body().setLinearVelocity(velocity.x * power, velocity.y * power);
		debugShapeRenderer.begin();
		for(int i = 0; i < 50; i++) {
			debugShapeRenderer.setColor(Color.BLUE);
			world.step(1.0f / 100.0f, 6, 2);
			debugShapeRenderer.circle(b.body().getPosition().x * PIXELS2METERS,
				b.body().getPosition().y * PIXELS2METERS, ball.sprite().getWidth() / 2);
			if(collisionDetector.isSimhit()) break;
		}
		debugShapeRenderer.end();
		world.destroyBody(b.body());
		world.clearForces();
	}

	Vector2 velocity = new Vector2(25,25);
	boolean drawBallOver = false;
	/**
	 * Update all of the variables needed to calculate sprite positioning
	 */
	public void update() {
		// Snap the times
		collisionDetector.updateTimes(deltaTime);

		// We need the mouse's Y to be normalized because LibGDX
		// defines the graphic's (0,0) to be at the _bottom_ left corner
		// while the input's (0,0) is at the _top_ left. So we do ScreenHeight - getY().
		mouse.set(Gdx.input.getX(), ScreenHeight - Gdx.input.getY());

		// If we have touched the screen or clicked we should update the power immediately
		// based on where the mouse was clicked and the current mouse position
		if(touch) {
			if(Gdx.input.isKeyPressed(Input.Keys.X)) {
				power = lastUsedPower;
			} else {
				power = (float)Math.sqrt(Math.pow((mouse.x - mouseClick.x), 2.0) +
					Math.pow((mouse.y - mouseClick.y), 2.0)) / 100.0f;
				if(power > MAX_POWER) power = MAX_POWER;
			}
			crosshair.setCenter(mouseClick.x, mouseClick.y);
		}

		// If the ball has not been shot, the mouse was not clicked and the gun is not shooting the ball
		// then we update the angle to the gun. This prevents the gun from rotating while the player is
		// dragging to set the power and prevents is from further rotation when the ball has been shot
		if(!shoot && !ballShot) {
			if(Gdx.input.isKeyPressed(Input.Keys.Z)) { // DEBUG ???
				angle = lastUsedAngle;
			} else {
				// Find the angle for the gun and ball's projection arc based on where the mouse is
				// located on the screen. Works best if the gun's texture is defaulted to point towards
				// the right.
				/*angle = (float) Math.atan2(mouseGraphicsY - gun.getCenterY(),
					                       mouse.x - gun.getCenterX());
				angle *= (180 / Math.PI);

				// Only allow the gun to rotate between 0 and 90 degrees
				if(angle > 90) angle = 90;
				if(angle < 0) angle = 0;*/
				angle = (float) Math.atan2(mouseClick.y - mouse.y,
					mouseClick.x - mouse.x);
				angle *= (180 / Math.PI);
				//if(angle < 0) angle = 360 - (-angle);
				if(angle > 90) angle = 90;
				else if(angle < 0) angle = 0;
			}
			gun.sprite().setRotation(angle); // Only set the rotation if the ball is not shot
		}

		velocity.setAngle(angle);
		// If we are going to shoot and the ball has not already been shot, shoot the ball.
		// We also need to update the last angle and power calculations
		if(shoot && !ballShot) {
			shoot = false;
			ballShot = true;
			ball.body().setType(BodyDef.BodyType.DynamicBody);
			//ball.body().applyLinearImpulse(shot(angle), ball.body().getWorldCenter(), true);
			ball.body().setLinearVelocity(velocity.x * power, velocity.y * power);
			lastUsedPower = power;
			lastUsedAngle = angle;
		}

		// Set the ball's sprite position the the same position as the ball's Box2D body position
		if(ballShot) {
			ball.setSpriteToBodyPosition();
			if((ball.body().getPosition().x * PIXELS2METERS) > gun.endX(angle) &&
				(ball.body().getPosition().y * PIXELS2METERS) > gun.endY(angle)) {
				drawBallOver = true;
			}
		}

		// Destroy the current ball in the world (if there is one) so another can be shot
		// Stop any sound (if it was playing)
		// This essentially "resets" the level
		if(Gdx.input.isKeyJustPressed(Input.Keys.F) && ballShot) {
			resetLevel();
		}

		if(moveBall) {
			moveBall();
		}

		if(playRipple) {
			ripple.setScale(ripple.getScaleX() + 0.1f, ripple.getScaleY() + 0.1f);
			if(ripple.getScaleX() >= 1 && ripple.getScaleY() >= 1) {
				ripple.getTexture().dispose();
				ripple = null;
				playRipple = false;
			}
		}

		// Go to next level if goal was hit
		if(goalHit || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) { // SPACE IS DEBUG
			unloadLevel();
			resetLevel();
			if(levelPtr == map.length-1) levelPtr = 0;
			else levelPtr++;
			loadLevel(levelPtr);
			goalHit = false;
			showGoalHit = true;
		}

		if(sim) simulate();
	}

	void unloadLevel() {
		Array<Body> bodyArray = new Array<Body>();
		world.getBodies(bodyArray);
		// Get all the bodies that are not a ball or boundary (all blocks) and destroy them.
		for(int i = 0; i < bodyArray.size; i++) {
			if(! bodyArray.get(i).getFixtureList().first().getUserData().equals("ball") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("boundary") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("boundaryBot")) {
				world.destroyBody(bodyArray.get(i));
			}
		}
	}

	void resetLevel() {
		ballShot = false;
		ball.body().setType(BodyDef.BodyType.StaticBody);
		moveBall = true;
		playNotes = true;
		drawBallOver = false;
		if(goalWasHit) {
			if(goalNoisePlaying) goalNoise.stop();
			goalNoisePlaying = false;
			goalWasHit = false;
		}
	}

	void moveBall() {
		ball.body().setTransform(lerp(ball.body().getPosition().x,
				(gun.getCenterX() / PIXELS2METERS),
				deltaTime * 10),
			lerp(ball.body().getPosition().y, (gun.getCenterY() / PIXELS2METERS), deltaTime * 10), 0.0f);
		ball.setSpriteToBodyPosition();
		shoot = false;
		if((ball.body().getPosition().x < ((gun.getCenterX() / PIXELS2METERS) + 0.02f)) &&
			(ball.body().getPosition().y < ((gun.getCenterY() / PIXELS2METERS) + 0.02f)))
		{
			ball.body().setTransform((gun.getCenterX() / PIXELS2METERS),
				(gun.getCenterY() / PIXELS2METERS), 0.0f);
			ball.setSpriteToBodyPosition();
			moveBall = false;
			sim = true;
		}
	}

	/**
	 * Render all of the objects in the game world.
	 */
	@Override
	public void render() {
		mapRenderer.setView(camera); // Set the view of the level built with Tiled to the main camera.
		// OpenGL
		//Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f); // DEBUG: Light Grey
		Gdx.gl.glClearColor(1, 1, 1, 1); // DEBUG: White
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();

		mapRenderer.render(); // Render the level built with Tiled first

		// Update all of the sprites
		update();

		// Update the debug strings
		inputDebug = "Mouse X: " + mouse.x + " | Mouse Y: " + mouse.y +
			" | Angle: " + String.format("%.2f", angle) +
			" | Last Angle: " + String.format("%.2f", lastUsedAngle);
		mouseClickDebug = "mouseClick: " + mouseClick + " | mouseUnClick: " +
			mouseUnClick + " | Power: " + power + " | Last Power: " + lastUsedPower;
		ballPositionDebug = "Ball X: " + ball.body().getPosition().x +
			" | Ball Y:" + ball.body().getPosition().y;
		gunPositionDebug = "Gun X: " + gun.getCenterX() + "(" + (gun.getCenterX() / PIXELS2METERS) + ")"
			+ " | Gun Y: " + gun.getCenterY() + "(" + (gun.getCenterY() / PIXELS2METERS) + ")";

		camera.update(); // Update the camera just before drawing

		debugShapeRenderer.begin();
		debugShapeRenderer.setColor(Color.RED);
		debugShapeRenderer.rect(gunDebugRectangle.getX(), gunDebugRectangle.getY(),
			gunDebugRectangle.getWidth(), gunDebugRectangle.getHeight());
		debugShapeRenderer.setColor(Color.ORANGE);
		debugShapeRenderer.arc(gun.getCenterX(), gun.getCenterY(), gun.sprite().getWidth() / 2,
			0.0f, angle, 32);
		debugShapeRenderer.setColor(Color.GREEN);
		debugShapeRenderer.circle(gun.endX(angle), gun.endY(angle), 3.0f);
		debugShapeRenderer.end();

		batch.begin();   // Start the batch drawing

		// Draw the ripple before the ball so it does not cover the ball
		if(playRipple) {
			batch.draw(ripple, ripple.getX(), ripple.getY(), ripple.getOriginX(), ripple.getOriginY(),
				ripple.getWidth(), ripple.getHeight(), ripple.getScaleX(), ripple.getScaleY(),
				ripple.getRotation());
		}

		// We have to set ALL of the ball's sprite's parameters because we are
		// using the batch to draw it, not drawing it in the batch.
		if(drawBallOver) {

			// Draw the gun first so it is under the ball
			gun.sprite().draw(batch);

			// Draw the ball second
			batch.draw(ball.sprite(), ball.sprite().getX(), ball.sprite().getY(),
				ball.sprite().getOriginX(), ball.sprite().getOriginY(), ball.sprite().getWidth(),
				ball.sprite().getHeight(), ball.sprite().getScaleX(), ball.sprite().getScaleY(),
				ball.sprite().getRotation());
		} else {
			// Draw the ball first so it is under the gun
			batch.draw(ball.sprite(), ball.sprite().getX(), ball.sprite().getY(),
				ball.sprite().getOriginX(), ball.sprite().getOriginY(), ball.sprite().getWidth(),
				ball.sprite().getHeight(), ball.sprite().getScaleX(), ball.sprite().getScaleY(),
				ball.sprite().getRotation());

			// Now draw the gun so it is over the ball
			gun.sprite().draw(batch);
		}

		if(touch) {
			crosshair.draw(batch);
			batch.end(); // Have to stop the sprite batch for the shape renderer lines to draw
			debugShapeRenderer.begin();
			debugShapeRenderer.setColor(Color.PURPLE);
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouse.x, mouse.y);
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x + 100, mouseClick.y); //+X
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x - 100, mouseClick.y); //-X
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x, mouseClick.y + 100); //+Y
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, mouseClick.x, mouseClick.y - 100); //-Y
			debugShapeRenderer.setColor(Color.ORANGE);
			debugShapeRenderer.arc(mouseClick.x, mouseClick.y, 100, 0.0f, angle, 32);
			debugShapeRenderer.end();
			batch.begin(); // Restart the sprite batch
		}

		// Draw debug inputs last so they are always on top
		if (showGoalHit) {
			goalWasHit = true;
			debugMessage.setColor(Color.RED);
			debugMessage.draw(batch, "GOAL!", ScreenWidth / 2, ScreenHeight / 2);
			if (goalTextTimer > 3.0f) { // Keep the text up for 10 seconds
				showGoalHit = false;
				goalTextTimer = 0.0f;
			}
			goalTextTimer += deltaTime;
		}
		debugMessage.setColor(Color.GREEN);
		debugMessage.draw(batch, inputDebug, 10, ScreenHeight - 10);
		debugMessage.draw(batch, mouseClickDebug, 10, ScreenHeight - 40);
		debugMessage.draw(batch, ballPositionDebug, 10, ScreenHeight - 70);
		debugMessage.draw(batch, gunPositionDebug, 10, ScreenHeight - 100);
		debugMessage.draw(batch, "Level :" + levelPtr, 10, ScreenHeight - 130);
		debugMessage.setColor(Color.YELLOW);
		debugMessage.draw(batch, fpsDebug + Gdx.graphics.getFramesPerSecond(), ScreenWidth - 60, ScreenHeight - 10);

		batch.end(); // Stop the batch drawing

		/*debugShapeRenderer.begin();
		debugShapeRenderer.setColor(Color.PURPLE);
		debugShapeRenderer.line(gun.getCenterX(), gun.getCenterY(), mouse.x, mouseGraphicsY);
		if(!touch) {

		} else {
			debugShapeRenderer.line(gun.getCenterX(), gun.getCenterY(),
				mouseClick.x, ScreenHeight - mouseClick.y);
		}
		debugShapeRenderer.end();*/

		// Simulate Box2D physics
		updatePhysics();
	}

	/**
	 * Gets the Box2D physics world for the game.
	 * @return The current Box2D world.
	 */
	public static World getWorld() {
		return world;
	}

	public static void setGoalHit(boolean b) {
		goalHit = b;
	}

	public static boolean playNotes() {
		return playNotes;
	}

	public static boolean goalNoisePlaying() {
		return goalNoisePlaying;
	}

	public static void playGoalNoise() {
		goalNoise.play();
		goalNoisePlaying = true;
		playNotes = false;
	}

	public static void playRipple(Fixture fb) {
		playRipple = true;
		ripple = new Sprite(new Texture(Gdx.files.internal("ripple.png")));
		ripple.setCenter((fb.getBody().getPosition().x * PIXELS2METERS),
			(fb.getBody().getPosition().y * PIXELS2METERS));
		ripple.setScale(0.1f, 0.1f);
	}

	public static void playNote(int i) {
		notes[i].play();
	}

	public static int getNotePtr() {
		return notePtr;
	}

	public static int notesLength() {
		return notes.length;
	}

	public static void addImpulseToBall(Fixture fa) {
		if(ball.body().getWorldCenter().y > (fa.getBody().getWorldCenter().y +
			fa.getShape().getRadius()) + 0.7) {

			ball.body().setLinearVelocity(ball.body().getLinearVelocity().x, 0);
			ball.body().applyLinearImpulse(new Vector2(0, 1.5f),
				ball.body().getWorldCenter(), true);
		}
	}

	public boolean keyDown (int keycode) {
		return false;
	}

	public boolean keyUp (int keycode) {
		return false;
	}

	public boolean keyTyped (char character) {
		return false;
	}

	boolean sim = true; // !!! Move up
	public boolean touchDown (int x, int y, int pointer, int button) {
		System.out.println("Touch down");
		if(ballShot) {
			System.out.println("reset");
			resetLevel();
			reset = true;
		} else {
			mouseClick.x = x; mouseClick.y = ScreenHeight - y;
			touch = true;
			reset = false;
		}
		return false;
	}

	public boolean touchUp (int x, int y, int pointer, int button) {
		System.out.println("Touch up");
		// If we are not doing a reset of the level (reset is true when we click inside of
		// the gun's boundary
		if(!reset) {
			shoot = true;
			touch = false;
			mouseUnClick.x = x; mouseUnClick.y = y;
			sim = false;
		}
		return false;
	}

	public boolean touchDragged (int x, int y, int pointer) {
		return false;
	}

	public boolean mouseMoved (int x, int y) {
		return false;
	}

	public boolean scrolled (int amount) {
		return false;
	}
}
