package com.parkerbrandt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.parkerbrandt.utilities.InputHandler;

import java.io.BufferedReader;
import java.io.IOException;


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


	/*
	 * Methods
	 */

	/**
	 * Finds the user's decklists in the 'files' directory, and returns them
	 * @return an array that contains each decklist, itself being an array of strings
	 */
	private Array<Array<String>> getDecklists() throws IOException {
		Array<Array<String>> allLists = new Array<>();

		// Get all the files, check if they are text files, and then retrieve the information from them
		FileHandle[] files = Gdx.files.local("files/").list();
		for (FileHandle file : files) {
			Array<String> decklist = new Array<>();

			if (file.extension().equalsIgnoreCase("txt")) {
				System.out.println("Reading " + file.nameWithoutExtension() + "...");

				// Read the file and retrieve the information
				BufferedReader reader = new BufferedReader(file.reader());

				String line = "";
				while ((line = reader.readLine()) != null) {
					// TODO: Need to separate between count and card name
					System.out.println("\tAdding " + line + " to " + file.nameWithoutExtension());
					decklist.add(line);
				}

				System.out.println("Finshed reading " + file.nameWithoutExtension());
			}
		}

		return allLists;
	}


	/*
	 * Override Methods
	 */

	@Override
	public void create () {

		// TODO: Need to have user choose mode (AI or Manual)

		// Retrieve the user's decklist info from the text files
		try {
			Array<Array<String>> decklists = getDecklists();
		} catch(IOException err) {
			System.out.println("Could not read from decklists...");
			System.out.println(err.getMessage());
		}

		// TODO: Need to have user choose from the available decklists

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
