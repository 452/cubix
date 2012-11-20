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
	Color buttonColor;

	GameCube cube;
	Game game;

	// Gameplay variables
	boolean introScreenOn = true, gameOn = false, audioOn = true;
	static final int MAX_COLOR_GAME = 0;

	//Some UI stuff 
	private Font bigFont = new Font("Helvetica", Font.BOLD,40);
	Image img;
	MediaTracker tr;

	public void initialize()
	{

		//String imagePath = getCodeBase()+"background.png";
		Renderer.setBg("background.png");
		
		tr = new MediaTracker(this);
		img = getImage(getCodeBase(), "score.png");
		tr.addImage(img,0);
	

		// Initialize world color and light variables
		//setBgColor(.7, .7, .9);
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
		this.maxColorGameButton.addPoint(250, 260);
		this.maxColorGameButton.addPoint(380, 260);
		this.maxColorGameButton.addPoint(380, 300);
		this.maxColorGameButton.addPoint(250, 300);

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
		g.drawString(this.audioOn ? "AUDIO: ON" : "AUDIO: OFF", this.audioButton.getBounds().x+10,
				this.audioButton.getBounds().y+20);


		// If on the intro screen, draw it
		if(this.introScreenOn)
		{
			// Get center x and y coordinates
			int centerWidth = this.getWidth()/2;
			int centerHeight = this.getHeight()/2;

			Font saveOld = g.getFont();
			g.setColor(Color.BLUE);
			g.setFont(bigFont);
			g.drawString("CUBIX",centerWidth-60,150);
			g.setFont(saveOld);


			// Draw the max color game button
			g.setColor(Color.WHITE);
			g.fillPolygon(this.maxColorGameButton);
			g.setColor(Color.BLACK);
			g.drawString("MAX COLOR GAME", this.maxColorGameButton.getBounds().x+10,
					this.maxColorGameButton.getBounds().y+25);
		}
		else if(this.gameOn)
		{
			// Draw the menu button
			g.setColor(this.buttonColor);
			g.fillPolygon(this.mainMenuButton);
			g.setColor(Color.BLACK);
			g.drawString("MENU", this.mainMenuButton.getBounds().x+10,
					this.mainMenuButton.getBounds().y+20);

			// If there is an active game, call its drawOverlay method too
			if(this.game != null)
			{
				this.game.drawOverlay(g);
				g.drawImage(img, 420, 360, this);
				g.fillArc(420,360,90,90,0,this.game.getScore());
				g.drawString("SCORE METER",420,420);
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
