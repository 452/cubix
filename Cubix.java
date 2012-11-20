/*
 * Computer Games - Fall 2012
 * Cubix - Games based on a 3D cube
 *
 * USES CUSTOM MODIFIED RENDER.GEOMETRY CLASS
 */

import elements.*;
import games.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Cubix extends RenderApplet
{
	double point[] = new double[3];
	boolean isCapturedClick = false;

	// Button variables
	Polygon audioButton;
	Polygon mainMenuButton;
	Polygon maxColorGameButton;
	Polygon matchingGameButton;
	Color buttonColor;

	Game game;

	// Gameplay variables
	boolean introScreenOn = true, gameOn = false, audioOn = true;
	static final int MAX_COLOR_GAME = 0, MATCHING_GAME = 1;

	public void initialize()
	{
		//String imagePath = getCodeBase()+"background.png";

		//Renderer.setBg("images/background1.png");
		Renderer.setBg("images/background2.jpg");

		// Initialize world color and light variables
		setBgColor(.7, .7, .9);
		addLight( 1, 1, 1, .8, .85, 1);
		addLight(-1,-1,-1, 1, 1, 1);

		// Set button color
		this.buttonColor = Color.WHITE;

		// Initialize game buttons as Polygons
		// Initialize audio button
		this.audioButton = new Polygon();
		this.audioButton.addPoint(10, 440);
		this.audioButton.addPoint(90, 440);
		this.audioButton.addPoint(90, 470);
		this.audioButton.addPoint(10, 470);

		// Initialize main menu button
		this.mainMenuButton = new Polygon();
		this.mainMenuButton.addPoint(10, 10);
		this.mainMenuButton.addPoint(70, 10);
		this.mainMenuButton.addPoint(70, 40);
		this.mainMenuButton.addPoint(10, 40);

		// Initialize the max color game button
		this.maxColorGameButton = new Polygon();
		this.maxColorGameButton.addPoint(250, 150);
		this.maxColorGameButton.addPoint(380, 150);
		this.maxColorGameButton.addPoint(380, 190);
		this.maxColorGameButton.addPoint(250, 190);

		// Initialize the matching game button
		this.matchingGameButton = new Polygon();
		this.matchingGameButton.addPoint(250, 210);
		this.matchingGameButton.addPoint(380, 210);
		this.matchingGameButton.addPoint(380, 250);
		this.matchingGameButton.addPoint(250, 250);

		// Call method to set up the intro/main screen
		this.initializeIntroScreen();
	}

	// Override the super class' stop method - stop any additional actions
	public void stop()
	{
		super.stop();

		if(this.game != null)
		{
			this.game.stop();
			this.game = null;
		}
	}

	private void initGame(int gameNum)
	{
		// Start the game selected
		if(gameNum == MAX_COLOR_GAME)
		{
			this.gameOn = true;
			this.game = new games.maxColorGame.MaxColorGame(this, this.getWorld(), this.audioOn);
			this.game.initGame();
		}
		else if(gameNum == MATCHING_GAME)
		{
			this.gameOn = true;

			// INSERT CODE TO START MATCHING GAME HERE !!!
			// this.game = new games.matchingGame.MatchingGame(this, this.getWorld(), this.audioOn);
			// this.game.initGame();
		}

	}

	private void stopGame()
	{
		if(this.game != null)
		{
			this.game.stop();
		}
	}

	// Method to set up and show the intro/main screen
	private void initializeIntroScreen()
	{
		// Stop and nullify the Game instance if still active
		if(this.game != null)
		{
			this.game.stop();
			this.game = null;
		}
		this.gameOn = false;

		// Set variable to show the intro screen
		this.introScreenOn = true;
	}

	public void drawOverlay(Graphics g)
	{
		// Draw the audio button regardless of status
		g.setColor(Color.WHITE);
		g.fillPolygon(this.audioButton);
		g.setColor(Color.BLACK);
		g.setFont(Fonts.TINY_FONT);
		g.drawString(this.audioOn ? "AUDIO: ON" : "AUDIO: OFF", this.audioButton.getBounds().x+10,
				this.audioButton.getBounds().y+20);

		// If on the intro screen, draw it
		if(this.introScreenOn)
		{
			// Get center x and y coordinates
			int centerWidth = this.getWidth()/2;
			int centerHeight = this.getHeight()/2;

			// Draw main cubix text
			Font saveOld = g.getFont();
			g.setColor(Color.BLUE);
			g.setFont(Fonts.BIG_FONT);
			g.drawString("CUBIX",centerWidth-70, 50);
			g.setFont(Fonts.SMALL_FONT);
			g.drawString("A collection of games based on a 3D cube", centerWidth-200, 80);

			// Draw the max color game button
			g.setColor(Color.WHITE);
			g.fillPolygon(this.maxColorGameButton);
			g.setColor(Color.BLACK);
			g.setFont(Fonts.TINY_FONT);
			g.drawString("MAX COLOR GAME", this.maxColorGameButton.getBounds().x+10,
					this.maxColorGameButton.getBounds().y+25);

			// Draw the matching game button
			g.setColor(Color.WHITE);
			g.fillPolygon(this.matchingGameButton);
			g.setColor(Color.BLACK);
			g.setFont(Fonts.TINY_FONT);
			g.drawString("MATCHING GAME", this.matchingGameButton.getBounds().x+15,
					this.matchingGameButton.getBounds().y+25);

			g.setFont(saveOld);
		}
		else if(this.gameOn)
		{
			// Draw the menu button
			g.setColor(this.buttonColor);
			g.fillPolygon(this.mainMenuButton);
			g.setColor(Color.BLACK);
			g.drawString("MENU", this.mainMenuButton.getBounds().x+15,
					this.mainMenuButton.getBounds().y+20);

			// If there is an active game, call its drawOverlay method too
			if(this.game != null)
			{
				this.game.drawOverlay(g);
			}
		}
	}

	public boolean mouseDown(Event e, int x, int y)
	{
		// If not game on yet, do nothing on mouse click down
		if(!this.gameOn)
			return true;

		// Else recognize a click is captured if clicked on a geometry object (a cube tile)
		Geometry g = queryCursor(point);
		if(g != null)
		{
			this.isCapturedClick = true;
			return true;
		}

		return false;
	}

	public boolean mouseDrag(Event e, int x, int y)
	{
		if(this.isCapturedClick)
			return true;
		return false;
	}

	public boolean mouseUp(Event e, int x, int y)
	{
		// Check if mouse up on the audio button regardless of status
		if(this.audioButton.contains(x, y))
		{
			this.toggleAudio();
		}

		// If still on the intro screen
		if(this.introScreenOn)
		{
			// Check if any of the buttons are clicked
			if(this.maxColorGameButton.contains(x, y))
			{
				this.introScreenOn = false;
				this.initGame(this.MAX_COLOR_GAME);
			}
			else if(this.matchingGameButton.contains(x, y))
			{
				this.introScreenOn = false;
				this.initGame(this.MATCHING_GAME);
			}

			return true;
		}
		else if(this.gameOn)
		{
			// If click up on the menu button
			if(this.mainMenuButton.contains(x, y))
			{
				this.initializeIntroScreen();
			}

			// Else if a click is captured
			if(this.isCapturedClick)
			{
				Geometry g = queryCursor(point);

				// And if clicked on a geometry object (a cube tile) and a game exists
				if(g != null && this.game != null)
				{
					// Call game's method to handle action when a tile is clicked
					this.game.clickTile(g.getFace(), g.getPositionRow(), g.getPositionColumn());
				}
			}

			isCapturedClick = false;
			return true;
		}
		return false;
	}

	public void animate(double time)
	{
		if(this.gameOn && this.game != null)
		{
			this.game.animate(time);
		}
	}

	void toggleAudio()
	{
		// invert audio status
		this.audioOn = !this.audioOn;

		// If game is on, toggle its audio as well
		if(this.gameOn && this.game != null)
			this.game.toggleAudio(this.audioOn);
	}
}
