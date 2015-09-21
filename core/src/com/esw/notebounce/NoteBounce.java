package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
	public static final float originalGravity = -200.0f;
	public static float gravity = originalGravity;

//=====================================================================================================//

	private static World world;
	private static boolean playNotes = true;
	private static boolean goalHit = false;
	private static boolean goalNoisePlaying = false;
	private static boolean playRipple = false;
	private static Sound[] notes = new Sound[8];
	private static int notePtr = 0;
	private static Sound goalNoise;
	private static Boundary bot;
	private static Boundary top;
	private static Boundary left;
	private static Boundary right;

	final int velocityIterations = 6;
	final int positionIterations = 2;

	private int ScreenWidth  = 0;
	private int ScreenHeight = 0;

	private OrthographicCamera camera; // Orthographic because 2D

	private Box2DDebugRenderer box2DDebugRenderer;
	Matrix4 debugMatrix; // For Box2D's debug drawing projection
	private BitmapFont debugMessage;
	private Rectangle gunDebugRectangle;
	private ShapeRenderer debugShapeRenderer;

	private SpriteBatch batch;
	private Gun gun;
	private static Ball ball;
	private static Sprite ripple;
	private Sprite crosshair;

	private CollisionDetection collisionDetector;

	private float goalTextTimer = 0.0f;
	private float deltaTime = 0.0f;
	float timestep = 300.0f;

	String inputDebug = "";        // DEBUG
	String mouseClickDebug = "";   // DEBUG
	String ballPositionDebug = ""; // DEBUG
	String gunPositionDebug = "";  // DEBUG
	String fpsDebug = "FPS: ";     // DEBUG

	private boolean goalWasHit = false;
	private boolean showGoalHit= false; // Show "GOAL!" text

	private boolean ballShot = false; // Is the ball shot?
	private boolean moveBall = false; // Toggle to lerp the ball back to the gun
	boolean drawBallOver = false; // Toggle to draw the ball over the gun after it has been shot

	TmxMapLoader mapLoader;
	private TiledMap map[] = new TiledMap[6];
	private OrthogonalTiledMapRenderer mapRenderer;
	private int levelPtr = 0;

	private Vector2 mouse = new Vector2(0,0);
	private Vector2 mouseClick = new Vector2(0,0);
	private Vector2 mouseUnClick = new Vector2(0,0);

	Vector2 velocity = new Vector2(1,1);
	private float angle = 0.0f;
	private float power = 0.0f; // Power of the shot
	final float MAX_POWER = 60.0f; // Maximum power of the shot
	private float lastUsedAngle = 45.0f;
	private float lastUsedPower = 12.5f;
	private boolean shoot = false;

	private boolean touch = false; // Is the mouse clicked or the screen has been touched?
	private boolean reset = false; // Toggle to reset level

	Array<Vector2> simcoords = new Array<Vector2>();

	Inputs inputs;

	Box box;

	boolean edit = false; // TODO create "edit" mode

//=====================================================================================================//

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

		collisionDetector = new CollisionDetection();
		// Because the world's timestep will be 1/300, we need to make gravity
		// _a lot_ more than the standard 9.8 or 10. Otherwise the ball will act
		// like it is in space after it slows down quite a bit. 200 gives a good balance.
		world = new World(new Vector2(0, gravity), true);
		world.setContactListener(collisionDetector);

		ball = new Ball(0, 0); // Create the ball first so the gun can use it's dimensions
		gun = new Gun(30.0f, 30.0f);
		gunDebugRectangle = gun.sprite().getBoundingRectangle();
		ball.setPos(gun.getCenterX(), gun.getCenterY());

		box = new Box(ScreenWidth / 2, ScreenHeight / 2, Box.Type.yellow);

		// Build the lines for the bouding box that makes it so the ball
		// does not go off the screen
		bot = new Boundary(0.0f, 0.0f, ScreenWidth, 0.0f, Boundary.Type.bot);
		top = new Boundary(0.0f, ScreenHeight, ScreenWidth, ScreenHeight, Boundary.Type.top);
		left = new Boundary(ScreenWidth, 0.0f, ScreenWidth, ScreenHeight, Boundary.Type.left);
		right = new Boundary(0.0f, 0.0f, 0.0f, ScreenHeight, Boundary.Type.right);

        goalNoise = Gdx.audio.newSound(Gdx.files.internal("notes/goal.mp3"));

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

		crosshair = new Sprite(new Texture(Gdx.files.internal("art/crosshair.png")));

		System.out.println("Total levels: " + map.length); // DEBUG
		createLevelArray();
		loadLevel(0);

		inputs = new Inputs(ScreenWidth, ScreenHeight);
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

	// TODO reimplement loadLevel
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
				String name = map[level].getLayers().get(i).getName();
				System.out.println("Loading object : " + name);

				if(name.equals("door")) {
					// TODO create door class ???
				} else {
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
		}

		shape.dispose(); // Make sure to dispose of the shape to free some now unused memory.
		System.out.println("Level " + level + " successfully loaded.");
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
		t = MathUtils.clamp((t - edge0) / (edge1 - edge0), 0.0f, 1.0f);
		// Evaluate polynomial
		return t * t * t * (t * ((t * 6) - 15) + 10);
	}

	void unloadLevel() {
		Array<Body> bodyArray = new Array<Body>();
		world.getBodies(bodyArray);
		// Get all the bodies that are not a ball or boundary (all blocks) and destroy them.
		for(int i = 0; i < bodyArray.size; i++) {
			if(! bodyArray.get(i).getFixtureList().first().getUserData().equals("ball") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("bot") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("top") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("left") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("right") &&
				! bodyArray.get(i).getFixtureList().first().getUserData().equals("gun")) {
				world.destroyBody(bodyArray.get(i));
			}
		}
	}

	void resetLevel() {
		ballShot = false;
		ball.body.setType(BodyDef.BodyType.StaticBody);
		moveBall = true;
		playNotes = true;
		drawBallOver = false;
		world.setGravity(new Vector2(0, originalGravity));
		if(goalWasHit) {
			if(goalNoisePlaying) goalNoise.stop();
			goalNoisePlaying = false;
			goalWasHit = false;
		}
	}

	void moveBall() {
		ball.body.setTransform(lerp(ball.body.getPosition().x, (gun.getCenterX() / PIXELS2METERS),
			deltaTime * 10), lerp(ball.body.getPosition().y, (gun.getCenterY() / PIXELS2METERS),
			deltaTime * 10), 0.0f);
		ball.setSpriteToBodyPosition();
		shoot = false;
		if((ball.body.getPosition().x < ((gun.getCenterX() / PIXELS2METERS) + 0.02f)) &&
			(ball.body.getPosition().y < ((gun.getCenterY() / PIXELS2METERS) + 0.02f)))
		{
			ball.body.setTransform((gun.getCenterX() / PIXELS2METERS),
				(gun.getCenterY() / PIXELS2METERS), 0.0f);
			ball.setSpriteToBodyPosition();
			moveBall = false;
		}
	}

	/**
	 * Update all of the variables needed to simulate physics
	 */
	public void updatePhysics() {
		if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) timestep = 3000.0f;
		else if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) timestep = 100.0f;
		else timestep = 300.0f;

		world.step(1.0f / timestep, velocityIterations, positionIterations);

		world.clearForces();
	}

	/* NOTE: There are a number of ways we could simulate the ball's path. Some techniques can be
	 *		 combined for a greater effect:
	 * 1) Use one world and toggle between a state of simulation and actually stepping the world
	 *		with the ball being shot. This way we can do collision detection and and use the real physics
	 *		of the ball. This would be the most difficult to implement efficiently. To draw the arc while
	 *		the ball is shooting we would need to combine this with technique 3 (See #4).
	 * 2) Make another world. This pretty much is the worst case scenario, though. We can do
	 * 		the physics calculations trivially, as we would do with technique 1, but in order to simulate
	 * 		collisions we would have to copy the original world entirely. This is the easiest to
	 * 		implement, however, as there is no worry of the worlds stepping on top of one another
	 *		and the path can always be drawn trivially.
	 * 3) Draw sprites at the x,y coordinates using kinematic equations. This option is the most
	 *		efficient but is not ideal because it is not as flexible and we would need to implement
	 *		either technique 1 or 2 to do collisions anyway. This is the least interesting option.
	 * 4) The _best_ way to do this would be to combine techniques. We could toggle the simulation
	 * 		by stepping the world when the mouse is clicked and draw sprites in the locations
	 * 	 	that the ball would go. This way we can also have collision detection and the arc would
	 * 	 	be visible after the ball has been shot. There would need to be an array of sprites
	 * 	 	that we would have to draw to, increasing space, and this would be the most complex out of
	 *		all techniques to do super efficiently.
	 *
	 * FIXME This does not work on Mac
	 */
	/**
	 * Run a physics simulation that calculates where the ball would go if it were to be shot
	 * with the current power and angle.
	 */
	void simulate() {
		ball.body.getFixtureList().first().setUserData("sim");
		ball.body.setType(BodyDef.BodyType.DynamicBody);
		ball.body.setLinearVelocity(velocity.x * power, velocity.y * power);
		simcoords.clear();
		for(int i = 0; i < 200; i++) {
			world.step(1.0f / timestep, velocityIterations, positionIterations);
			simcoords.add(new Vector2(ball.body.getPosition().x * PIXELS2METERS,
				ball.body.getPosition().y * PIXELS2METERS));
			if(collisionDetector.simhit) break;
		}
		collisionDetector.simhit = false;
		ball.body.setType(BodyDef.BodyType.StaticBody);
		ball.body.setTransform(gun.getCenterX() / NoteBounce.PIXELS2METERS,
			gun.getCenterY() / NoteBounce.PIXELS2METERS, 0.0f);
		ball.body.getFixtureList().first().setUserData("ball");
		world.clearForces();
	}

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

		// If we have touched the screen or clicked we should update the power and the angle.
		if(touch) {
			if(Gdx.input.isKeyPressed(Input.Keys.X)) {
				power = lastUsedPower;
			} else {
				power = (float)Math.sqrt(Math.pow((mouse.x - mouseClick.x), 2.0) +
					Math.pow((mouse.y - mouseClick.y), 2.0)) / 4.0f;
				if(power > MAX_POWER) power = MAX_POWER;
			}
			crosshair.setCenter(mouseClick.x, mouseClick.y);

			if(Gdx.input.isKeyPressed(Input.Keys.Z)) { // DEBUG
				angle = lastUsedAngle;
			} else {
				// Find the angle for the gun and ball's projection arc based on where the mouse is
				// located on the screen. Works best if the gun's texture is defaulted to point towards
				// the right.
				angle = (float) Math.atan2(mouseClick.y - mouse.y,
					mouseClick.x - mouse.x);
				angle *= (180 / Math.PI);
				// Clamp the rotation around 360 degrees
				//if(angle < 0) angle = 360 - (-angle); // OLD (but useful??)
				// Only allow the gun to rotate between 0 and 90 degrees
				if(angle > 90) angle = 90;
				else if(angle < 0) angle = 0;
			}
			gun.rotate(angle); // Only set the rotation if the ball is not shot
		}

		velocity.setAngle(angle);
		// If we are going to shoot and the ball has not already been shot, shoot the ball.
		// We also need to update the last angle and power calculations
		if(shoot && !ballShot) {
			shoot = false;
			ballShot = true;
			ball.body.setType(BodyDef.BodyType.DynamicBody);
			//ball.body().applyLinearImpulse(shot(angle), ball.body().getWorldCenter(), true);
			ball.body.setLinearVelocity(velocity.x * power, velocity.y * power);
			lastUsedPower = power;
			lastUsedAngle = angle;
		}

		// Set the ball's sprite position the the same position as the ball's Box2D body position
		if(ballShot) {
			ball.setSpriteToBodyPosition();
			if((ball.body.getPosition().x * PIXELS2METERS) > gun.endX(angle) &&
				(ball.body.getPosition().y * PIXELS2METERS) > gun.endY(angle)) {
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

		if(touch) simulate();
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

		if(Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) edit = !edit;

		if(!edit) {
			// Update all of the sprites
			update();
			// Simulate Box2D physics
			if(ballShot) updatePhysics();
		}

		// Update the debug strings
		inputDebug = "mouse X: " + mouse.x + " | mouse Y: " + mouse.y +
			" | Angle: " + String.format("%.2f", angle) +
			" | Last Angle: " + String.format("%.2f", lastUsedAngle);
		mouseClickDebug = "mouseClick: " + mouseClick + " | mouseUnClick: " +
			mouseUnClick + " | Power: " + power + " | Last Power: " + lastUsedPower;
		ballPositionDebug = "Ball X: " + ball.body.getPosition().x +
			" | Ball Y:" + ball.body.getPosition().y;
		gunPositionDebug = "Gun X: " + gun.getCenterX() + "(" + (gun.getCenterX() / PIXELS2METERS) + ")"
			+ " | Gun Y: " + gun.getCenterY() + "(" + (gun.getCenterY() / PIXELS2METERS) + ")";

		camera.update(); // Update the camera just before drawing

		debugShapeRenderer.begin();
		debugShapeRenderer.setColor(Color.RED);
		debugShapeRenderer.rect(gunDebugRectangle.getX(), gunDebugRectangle.getY(),
			gunDebugRectangle.getWidth(), gunDebugRectangle.getHeight());
		debugShapeRenderer.setColor(Color.ORANGE);
		debugShapeRenderer.arc(gun.getCenterX(), gun.getCenterY(), gun.sprite().getWidth() / 2, 0.0f,
			angle, 32);
		debugShapeRenderer.setColor(Color.GREEN);
		debugShapeRenderer.circle(gun.endX(angle), gun.endY(angle), 3.0f);
		debugShapeRenderer.setColor(Color.BLUE);
		for(int i = 0; i < simcoords.size; i++) {
			Vector2 tmp = simcoords.get(i);
			debugShapeRenderer.circle(tmp.x, tmp.y, ball.sprite.getWidth()/2);
		}
		debugShapeRenderer.end();

		batch.begin();   // Start the batch drawing
		box.sprite.draw(batch);
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
			batch.draw(ball.sprite, ball.sprite.getX(), ball.sprite.getY(),
				ball.sprite.getOriginX(), ball.sprite.getOriginY(), ball.sprite.getWidth(),
				ball.sprite.getHeight(), ball.sprite.getScaleX(), ball.sprite.getScaleY(),
				ball.sprite.getRotation());
		} else {
			// Draw the ball first so it is under the gun
			batch.draw(ball.sprite, ball.sprite.getX(), ball.sprite.getY(),
				ball.sprite.getOriginX(), ball.sprite.getOriginY(), ball.sprite.getWidth(),
				ball.sprite.getHeight(), ball.sprite.getScaleX(), ball.sprite.getScaleY(),
				ball.sprite.getRotation());

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
		String g = "";
		if(world.getGravity().x == 0) {
			if(world.getGravity().y > 0) g = "Up";
			else g = "Down";
		}
		else {
			if(world.getGravity().x > 0) g = "Right";
			else g = "Left";
		}
		debugMessage.draw(batch, "Gravity : " + g, 10, ScreenHeight - 160);
		if(edit) debugMessage.draw(batch, "Mode: edit", 10, ScreenHeight - 190);
		else debugMessage.draw(batch, "Mode: play", 10, ScreenHeight - 190);
		debugMessage.setColor(Color.YELLOW);
		debugMessage.draw(batch, fpsDebug + Gdx.graphics.getFramesPerSecond(), ScreenWidth - 60,
			ScreenHeight - 10);

		batch.end(); // Stop the batch drawing

		// Copy the camera's projection and scale it to the size of the Box2D world
		debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS2METERS, PIXELS2METERS, 0);
		box2DDebugRenderer.render(world, debugMatrix); // Render the Box2D debug shapes
	}

	/**
	 * Gets the Box2D physics world for the game.
	 * @return The current Box2D world.
	 */
	public static World getWorld() { return world; }

	public static Ball getBall() { return ball; }

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
		ripple = new Sprite(new Texture(Gdx.files.internal("art/ripple.png")));
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
		ball.body.setLinearVelocity(ball.body.getLinearVelocity().x, 0);
		ball.body.applyLinearImpulse(new Vector2(0, 1.5f), ball.body.getWorldCenter(), true);
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

	public boolean touchDown (int x, int y, int pointer, int button) {
		if(ballShot) {
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
		// If we are not doing a reset of the level (reset is true when we click inside of
		// the gun's boundary
		if(!reset) {
			shoot = true;
			touch = false;
			mouseUnClick.x = x; mouseUnClick.y = y;
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
