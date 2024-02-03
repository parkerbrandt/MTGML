package com.parkerbrandt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.parkerbrandt.utilities.InputHandler;
import org.w3c.dom.css.Rect;

import java.util.Iterator;


/**
 * Main class of the "Learning Magic the Gathering" Project
 * Handles core update loop of the project (creation of objects and rendering)
 */
public class MTGSim extends ApplicationAdapter {

	/*
	 * Class variables
	 */
	public static final int WIDTH = 800;
	public static final int HEIGHT = 480;

	/*
	 * Properties
	 */

	private InputHandler inputHandler;

	// Assets
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	// Game Objects
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;


	/*
	 * Methods
	 */

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, WIDTH - 64);
		raindrop.y = HEIGHT;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}


	/*
	 * Override Methods
	 */

	@Override
	public void create () {

		// TODO: Get all relevant Magic cards

		// Initialize the interaction handler (handles mouse and keyboard input)
		inputHandler = new InputHandler();

		// Load images
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// Load sound effects and music
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// Start playback of background music
		rainMusic.setLooping(true);
		rainMusic.play();

		// Create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH, HEIGHT);

		// Create the sprite batch to draw the 2D images
		batch = new SpriteBatch();

		// Initialize our bucket
		bucket = new Rectangle();
		bucket.x = WIDTH/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		// Begin to create raindrops
		raindrops = new Array<>();
		spawnRaindrop();
	}

	@Override
	public void render () {
		// Clear screen with a dark blue color
		ScreenUtils.clear(0, 0, 0.2f, 1);

		camera.update();

		// Draw the bucket
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// Allow for mouse interaction
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		// Allow for keyboard input
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// Check boundaries
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > WIDTH - 64) bucket.x = 800 -64;

		// Check if we should spawn another raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		// Make the raindrops move
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext();) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();

			if (raindrop.y + 64 < 0) iter.remove();

			// Check for collisions with the bucket
			if (raindrop.overlaps(bucket)) {
				dropSound.play();
				iter.remove();
			}
		}
	}
	
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}
}
