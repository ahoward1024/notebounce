package com.esw.notebounce;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

import java.util.Timer;

import javax.rmi.CORBA.Util;

import sun.security.krb5.SCDynamicStoreConfig;

/**
 * Created by Alex on 9/21/2015.
 * Copyright echosoftworks 2015
 */
@SuppressWarnings("unused")
public class NoteBounce extends ApplicationAdapter implements InputProcessor {

	public final static float PIXELS2METERS = 100.0f; // Yay globals!
	public static final int basex = 1920, basey = 1080;
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

	final int velocityIterations = 6;
	final int positionIterations = 2;

	static int ScreenWidth  = 0;
	static int ScreenHeight = 0;

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
	final float timestepNormal = 300.0f;
	final float timestepSlow = 3000.0f;
	final float timestepFast = 100.0f;

	String inputDebug = "";        // DEBUG
	String mouseClickDebug = "";   // DEBUG
	String ballPositionDebug = ""; // DEBUG
	String ballVelocityDebug = ""; // DEBUG
	String gunPositionDebug = "";  // DEBUG
	String fpsDebug = "FPS: ";     // DEBUG

	private boolean goalWasHit = false;
	private boolean showGoalHit= false; // Show "GOAL!" text

	private boolean ballShot = false; // Is the ball shot?
	private boolean moveBall = false; // Toggle to lerp the ball back to the gun
	boolean drawBallOver = false; // Toggle to draw the ball over the gun after it has been shot

	// TODO create LevelLoader

	private Vector2 mouseClick = new Vector2(0,0);
	private Vector2 mouseUnClick = new Vector2(0,0);

	Vector2 velocity = new Vector2(1,1);
	private float angle = 0.0f;
	private float power = 0.0f; // Power of the shot
	private float MAX_POWER = 60.0f; // Maximum power of the shot
	private float lastUsedAngle = 45.0f;
	private float lastUsedPower = 12.5f;
	private boolean shoot = false;

	private boolean touch = false; // Is the mouse clicked or the screen has been touched?
	private boolean reset = false; // Toggle to reset level

	Array<Vector2> simcoords = new Array<Vector2>();

	Box[] boxes;

	boolean edit = false; // TODO create "edit" mode
	boolean snap; // Snapping to grid on/off

	int lines = 0;
	int midlines = 0;

	// FIXME screen resolutions differ.
	// 2560x1440, Scale: 133% at 160px
	// 1920x1080, Scale: 100% at 120px (base resolution)
	// 1280x720, Scale: 66% at 80px
	// 1280x768, Scale: 53% at 64px
	// 800x480, Scale: 33% at 40px
	// FIXME this will be accomplished best by making a scale parameter and scaling all objects
	// fixme to their appropriate size based on the device's screen resolution.

	static float scalePercent = 0;

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
		scalePercent = Utility.findScalePercent(ScreenWidth, ScreenHeight);
		lines = (int)(Utility.GCD(basex, basey) * scalePercent);
		midlines = lines / 2;

		System.out.println(scalePercent + "%");

		MAX_POWER *= scalePercent;

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

		ball = new Ball(0, 0, scalePercent); // Create the ball first so the gun can use it's dimensions
		gun = new Gun(30.0f, 30.0f, scalePercent);
		gunDebugRectangle = gun.sprite().getBoundingRectangle();
		ball.setPos(gun.getCenterX(), gun.getCenterY());

		boxes = new Box[6];
		boxes[0] = new Box(ScreenWidth / 4, ScreenHeight / 2, scalePercent, Box.Style.yellow);
		boxes[1] = new Box(ScreenWidth - midlines, ScreenHeight - midlines, scalePercent, Box.Style.goal);
		boxes[2] = new Box(midlines, ScreenHeight - midlines, scalePercent, Box.Style.blue);
		boxes[3] = new Box(ScreenWidth - midlines, midlines, scalePercent, Box.Style.green);
		boxes[4] = new Box(ScreenWidth / 2, ScreenHeight - midlines, scalePercent, Box.Style.magenta);
		boxes[5] = new Box(ScreenWidth / 2, midlines, scalePercent, Box.Style.cyan);

		// Build the lines for the bounding box that makes it so the ball
		// does not go off the screen
		new Boundary(0.0f, 0.0f, ScreenWidth, 0.0f, UserData.Edge.bot);
		new Boundary(0.0f, ScreenHeight, ScreenWidth, ScreenHeight, UserData.Edge.top);
		new Boundary(ScreenWidth, 0.0f, ScreenWidth, ScreenHeight, UserData.Edge.left);
		new Boundary(0.0f, 0.0f, 0.0f, ScreenHeight, UserData.Edge.right);

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

		LevelLoader loader = new LevelLoader("levels/");
	}

	@Override
	public void resize(int width, int height) {
		//ScreenWidth = width;
		//ScreenHeight = height;
		//System.out.println(width + "x" + height);
		//scalePercent = Utility.findScalePercent(ScreenWidth, ScreenHeight);
	}

	/**
	 * Reset the state of the current level so the ball can be shot again.
	 */
	void reset() {
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

	/**
	 * Move the ball through linear interpolation back to the gun.
	 */
	void moveBall() {
		ball.body.setTransform(Utility.lerp(ball.body.getPosition().x,
			(gun.getCenterX() / PIXELS2METERS),
			deltaTime * 10), Utility.lerp(ball.body.getPosition().y, (gun.getCenterY() / PIXELS2METERS),
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
	 * Update all of the variables needed to simulate physics.
	 */
	public void updatePhysics() {
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
	 */
	/**
	 * Run a physics simulation that calculates where the ball would go if it were to be shot
	 * with the current power and angle.
	 */
	Color drawColor = Color.BLUE; // !!! MOVE
	void simulate() {
		ball.body.getFixtureList().first().setUserData(new UserData(UserData.Type.sim));
		ball.body.setType(BodyDef.BodyType.DynamicBody);
		ball.body.setLinearVelocity(velocity.x * power, velocity.y * power);
		simcoords.clear();

		int steps = 4;
		if(timestep == timestepNormal) { steps = 8; drawColor = Color.BLUE; }
		else if(timestep == timestepSlow) { steps = 20; drawColor = Color.PURPLE; }
		else if(timestep == timestepFast) { steps = 2; drawColor = Color.RED; }
		// NOTE: DO NOT SET THE LOOP THIS HIGH (> 500) FOR A RELEASE BUILD. If the gun is aimed straight
		// up the loop will not break causing it to run every iteration and will cause framerate issues.
		for(int i = 0; i < 300; i++) { // DEBUG
			world.step(1.0f / timestep, velocityIterations, positionIterations);
			if(i % steps == 0) {
				simcoords.add(new Vector2(ball.body.getPosition().x * PIXELS2METERS,
					ball.body.getPosition().y * PIXELS2METERS));
			}
			if(collisionDetector.simhit) break;
		}
		collisionDetector.simhit = false;
		ball.body.setType(BodyDef.BodyType.StaticBody);
		ball.body.setTransform(gun.getCenterX() / NoteBounce.PIXELS2METERS,
			gun.getCenterY() / NoteBounce.PIXELS2METERS, 0.0f);
		ball.body.getFixtureList().first().setUserData(new UserData(UserData.Type.ball));
		world.clearForces();
	}

	/**
	 * Update all of the variables needed to calculate sprite positioning
	 */
	public void update() {
		// Snap the times
		collisionDetector.updateTimes(deltaTime);

		// If we have touched the screen or clicked we should update the power and the angle.
		if(touch) {
			// The power is calculated by getting the distance between where the touch input started
			// and where the touch input is currently at, then divided by four to "smooth" out the value
			// so the player can have a more granular shot based on how far they pulled. 15.0f is added
			// so the ball will _always_ shoot out of the gun. This is so there are not accidental
			// misstaps where the player might not know if they have shot the ball or not
			power = ((float)Math.sqrt(Math.pow((Inputs.mouse.x - mouseClick.x), 2.0) +
				Math.pow((Inputs.mouse.y - mouseClick.y), 2.0)) / 4.0f) + 15.0f;
			if(power > MAX_POWER) power = MAX_POWER;

			crosshair.setCenter(mouseClick.x, mouseClick.y);

			// Find the angle for the gun and ball's projection arc based on where the mouse is
			// located on the screen. Works best if the gun's texture is defaulted to point towards
			// the right.
			angle = (float) Math.atan2(mouseClick.y - Inputs.mouse.y,
				mouseClick.x - Inputs.mouse.x);
			angle *= (180 / Math.PI);
			// Clamp the rotation around 360 degrees
			//if(angle < 0) angle = 360 - (-angle); // OLD (but useful??)
			// Only allow the gun to rotate between 0 and 90 degrees
			if(angle > 90) angle = 90;
			else if(angle < 0) angle = 0;

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
		if(Inputs.f && ballShot) {
			reset();
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
		if(goalHit || Inputs.space) { // SPACE IS DEBUG
			reset();
			// todo LevelLoader
			// todo loop levels
			//levelLoader.loadNextLevel();
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

		// OpenGL
		//Gdx.gl.glClearColor(0.7f, 0.7f, 0.7f, 0.7f); // DEBUG: Light Grey
		Gdx.gl.glClearColor(1, 1, 1, 1); // DEBUG: White
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		deltaTime = Gdx.graphics.getDeltaTime();

		// ================ UPDATE ================//

		// WARNING!!!!! ALWAYS GRAB INPUTS FIRST!! If you do not this could have dire consequences
		// as the input states will not be updated since the last frame which could cause keys
		// to always be pressed or never be pressed etc...
		if(Inputs.edit()) edit = !edit; // Grab the edit key (grave) first

		if(!edit) {
			// Update all of the sprites
			Inputs.getGameInputs();
			if(Inputs.lshift) timestep = timestepSlow;
			else if (Inputs.lctrl) timestep = timestepFast;
			else timestep = timestepNormal;
			update();
			// Simulate Box2D physics
			if(ballShot) updatePhysics();
		} else {
			Inputs.getEditInputs(); // TODO edit inputs

			debugShapeRenderer.begin();
			debugShapeRenderer.setColor(new Color(1, 0, 0, 0.1f));
			for(int i = 0; i < ScreenWidth; i += midlines) {
				debugShapeRenderer.line(i, 0, i, ScreenHeight);
				debugShapeRenderer.line(0, i, ScreenWidth, i);
			}
			debugShapeRenderer.setColor(new Color(0.5f, 0.5f, 0.5f, 0.1f));
			for(int i = 0; i < ScreenWidth; i += lines) {
				debugShapeRenderer.line(i, 0, i, ScreenHeight);
				debugShapeRenderer.line(0, i, ScreenWidth, i);
			}
			debugShapeRenderer.end();
		}

		// ================ RENDER ================//

		// Update the debug strings
		inputDebug = "mouse X: " + Inputs.mouse.x + " | mouse Y: " + Inputs.mouse.y +
			" | Angle: " + String.format("%.2f", angle) +
			" | Last Angle: " + String.format("%.2f", lastUsedAngle);
		mouseClickDebug = "mouseClick: " + mouseClick + " | mouseUnClick: " +
			mouseUnClick + " | Power: " + power + " | Last Power: " + lastUsedPower;
		ballPositionDebug = "Ball X: " + ball.body.getPosition().x + " (" + ball.sprite.getX() + ") " +
			" | Ball Y:" + ball.body.getPosition().y + " (" + ball.sprite.getY() +")";
		ballVelocityDebug = "Ball Velocity X: " + ball.body.getLinearVelocity().x + " | " +
			"Ball Velocity Y: " + ball.body.getLinearVelocity().y;
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
		debugShapeRenderer.setColor(drawColor);
		for(int i = 0; i < simcoords.size; i++) {
			Vector2 tmp = simcoords.get(i);
			debugShapeRenderer.circle(tmp.x, tmp.y, (ball.sprite.getWidth()/2) * scalePercent);
		}
		debugShapeRenderer.end();

		batch.begin();   // Start the batch drawing
		// Draw the boxes array
		for(Box b : boxes) {
			b.sprite.draw(batch);
		}
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
			debugShapeRenderer.line(mouseClick.x, mouseClick.y, Inputs.mouse.x, Inputs.mouse.y);
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
		debugMessage.draw(batch, ballVelocityDebug, 10, ScreenHeight - 100);
		debugMessage.draw(batch, gunPositionDebug, 10, ScreenHeight - 130);
		//debugMessage.draw(batch, "Level :" + LevelLoader.currentLevel(), 10, ScreenHeight - 130);
		String g;
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
		debugMessage.setColor(Color.RED);
		debugMessage.draw(batch, "Width: " + ScreenWidth + " | Height: " + ScreenHeight, ScreenWidth / 2,
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

	public static void playRipple() {
		playRipple = true;
		ripple = new Sprite(new Texture(Gdx.files.internal("art/ripple.png")));
		ripple.setCenter((ball.body.getPosition().x * PIXELS2METERS),
			(ball.body.getPosition().y * PIXELS2METERS));
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

	public enum ImpulseType {
		up,
		down,
		left,
		right
	}
	public static void addImpulseToBall(ImpulseType type) { // FIXME resolution independence
		System.out.println("before: " + ball.body.getLinearVelocity());
		float additionalImpulseForce = 1.25f * scalePercent;
		Vector2 direction = new Vector2(0,0);
		switch(type) {
			case up: {
				ball.body.setLinearVelocity(ball.body.getLinearVelocity().x, 0.0f);
				direction.set(0.0f, additionalImpulseForce);
			} break;
			case down: {
				ball.body.setLinearVelocity(ball.body.getLinearVelocity().x, 0.0f);
				direction.set(0.0f, -additionalImpulseForce);
			} break;
			case left: {
				ball.body.setLinearVelocity(0.0f, ball.body.getLinearVelocity().y);
				direction.set(-additionalImpulseForce, 0.0f);
			} break;
			case right: {
				ball.body.setLinearVelocity(0.0f, ball.body.getLinearVelocity().y);
				direction.set(additionalImpulseForce, 0.0f);
			} break;
		}
		ball.body.applyLinearImpulse(direction, ball.body.getWorldCenter(), true);
		System.out.println("after: " + ball.body.getLinearVelocity());
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
		if(!edit) {
			if(ballShot) {
				reset();
				reset = true;
			} else {
				mouseClick.x = x;
				mouseClick.y = ScreenHeight - y;
				touch = true;
				reset = false;
			}
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
