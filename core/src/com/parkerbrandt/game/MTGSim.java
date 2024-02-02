package com.parkerbrandt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

public class MTGSim extends ApplicationAdapter {

	/*
	 * Properties
	 */

	// Assets
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;

	private OrthographicCamera camera;
	private SpriteBatch batch;

	// Game Objects
	private Rectangle bucket;


	/*
	 * Override Methods
	 */


	@Override
	public void create () {
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
		camera.setToOrtho(false, 800, 480);

		// Create the sprite batch to draw the 2D images
		batch = new SpriteBatch();

		// Initialize our bucket
		bucket = new Rectangle();
		bucket.x = 800/2 - 64/2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;
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
		batch.end();

		// Allow for mouse interaction
	}
	
	@Override
	public void dispose () {

	}
}
