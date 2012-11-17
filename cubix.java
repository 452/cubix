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

public class cubix extends RenderApplet
{
	double point[] = new double[3];
	boolean isCapturedClick = false;

	GameCube cube;
	Game game;

	// Gameplay variables
	boolean introScreenOn = true, gameOn = false, gameOver = false, gameWin = false;

	static final int MAX_COLOR_GAME = 0;

	public void initialize()
	{
		setBgColor(.7, .7, .9);
		addLight( 1, 1, 1, .8, .85, 1);
		addLight(-1,-1,-1, 1, 1, 1);

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
			this.game = new games.maxColorGame.MaxColorGame(this.getWorld());
			this.game.initGame();

			//this.stopGame();
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

		// Set variable to show the intro screen
		this.introScreenOn = true;
	}

	public void drawOverlay(Graphics g)
	{
		// If on the intro screen, draw it
		if(this.introScreenOn)
		{
			int centerWidth = this.getWidth()/2;
			int centerHeight = this.getHeight()/2;
			g.setColor(Color.WHITE);
			g.fillRect(centerWidth-40, centerHeight-20, 80, 40);
			g.setColor(Color.BLACK);
			g.drawString("START", centerWidth-20, centerHeight);
		}

		// If there is an active game, call its drawOverlay method too
		if(this.game != null)
		{
			this.game.drawOverlay(g);
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
		// If still on the intro screen
		if(this.introScreenOn)
		{
			// Get dimensions of start game button
			int startX = this.getWidth()/2-40, endX = startX+80;
			int startY = this.getHeight()/2-20, endY = startY+40;

			// Check if clicked within the bounds of the start game button
			if(x >= startX && x <= endX && y >= startY && y <= endY)
			{
				// Disable intro screen and start game if so
				this.introScreenOn = false;
				this.initGame(this.MAX_COLOR_GAME);
			}

			return true;
		}
		else
		{
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

				isCapturedClick = false;
				return true;
			}
		}
		return false;
	}

	public void animate(double time)
	{
		if(this.game != null)
		{
			this.game.animate(time);
		}
	}
}
