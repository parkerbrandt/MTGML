package com.parkerbrandt.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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
import com.parkerbrandt.screens.MainScreen;
import com.parkerbrandt.screens.MatchScreen;
import com.parkerbrandt.screens.SettingsScreen;
import com.parkerbrandt.utilities.InputHandler;

import io.magicthegathering.javasdk.api.CardAPI;
import io.magicthegathering.javasdk.api.MTGAPI;
import io.magicthegathering.javasdk.resource.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


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
	private Array<Array<String>> decklists;
	private Array<Array<Card>> mtgDecklists;

	// Screens
	private Screen currentScreen;

	private MainScreen mainScreen;
	private SettingsScreen settingsScreen;
	private MatchScreen matchScreen;

	// Game Objects
	private InputHandler inputHandler;
	private OrthographicCamera camera;
	private SpriteBatch batch;

	private Rectangle bucket;
	private Array<Rectangle> cards;

	// Assets
	private Texture bucketImage;


	/*
	 * Methods
	 */

	/**
	 * Returns the Magic card, given the name of the card
	 * @param name the name of the card (i.e. "Cyclonic Rift" or "Kodama's Reach")
	 * @return the full Card object
	 */
	private Card getCardByName(String name) {
		List<Card> cards = CardAPI.getAllCards(Arrays.asList(name));
		return cards.get(0);
	}

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
					System.out.println("\tAdding " + line + " to " + file.nameWithoutExtension());
					decklist.add(line);
				}

				System.out.println("Finshed reading " + file.nameWithoutExtension());
			}
		}

		return allLists;
	}

	/*
	 * Screen Methods
	 */
	private void setMainScreen() {
		mainScreen = new MainScreen(this);
		currentScreen = mainScreen;
	}

	private void setSettingsScreen() {
		settingsScreen = new SettingsScreen(this);

	}

	private void setMatchScreen() {
		matchScreen = new MatchScreen(this);
	}


	/*
	 * Override Methods
	 */

	@Override
	public void create () {

		// Set up Magic API connection timeouts
		MTGAPI.setConnectTimeout(60);
		MTGAPI.setReadTimeout(60);
		MTGAPI.setWriteTimeout(60);

		// TODO: Need to have user choose mode (AI or Manual)

		// Retrieve the user's decklist info from the text files
		try {
			decklists = getDecklists();
		} catch(IOException err) {
			System.out.println("Could not read from decklists...");
			System.out.println(err.getMessage());
		}

		// TODO: Need to have user choose from the available decklists

		// Load each decklist as MTG cards from the MTG API
		mtgDecklists = new Array<>();
		for (Array<String> decklist : decklists) {
			Array<Card> mtgList = new Array<>();

			// Iterate through list and load cards
			for (String cardName : decklist) {
				// Need to separate the quantity from the actual name
				String[] data = cardName.split(" ");

				int quantity = Integer.parseInt(data[0]);
				cardName = cardName.replace(quantity + " ", "");

				// Load the card
				Card card = getCardByName(cardName);

				// Add the card that quantity many times
				for (int i = 0; i < quantity; i++)
					mtgList.add(card);
			}

			mtgDecklists.add(mtgList);
		}

		setMainScreen();

		// Initialize the interaction handler (handles mouse and keyboard input)
		inputHandler = new InputHandler();

		// Load images
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// Load sound effects and music

		// Start playback of background music

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

		currentScreen.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void dispose () {
		bucketImage.dispose();
		batch.dispose();

		mainScreen.dispose();
		settingsScreen.dispose();
		matchScreen.dispose();
	}
}
