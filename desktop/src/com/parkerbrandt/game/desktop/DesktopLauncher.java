package com.parkerbrandt.game.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.parkerbrandt.game.MTGSim;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		// Set window parameters
		config.setTitle("Learning Magic: The Gathering");
		config.setWindowedMode(800, 480);
		config.useVsync(true);
		config.setForegroundFPS(60);

		new Lwjgl3Application(new MTGSim(), config);
	}
}
