package com.vitu.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("SpaceShip:::Game::Java::DGX");

		//aqui eu posso configurar tudo em relação a janela do jogo utilizando o 'config'

		//para aumentar o tamanho da janela do jogo
		config.setWindowedMode(1280,720);


		new Lwjgl3Application(new SpaceShip(), config);
	}
}
