package games.maxColorGame;

import games.*;
import elements.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.applet.AudioClip;

public class MaxColorGame extends Game implements ActionListener
{
	java.applet.Applet app;

	private int level = 1, numCubeDimensions = 4;

	// Gameplay variables
	private boolean gameOn = false, gameOver = false, gameWin = false;

	// Audio variables
	private AudioClip backgroundAudio;
	private boolean audioOn = false;

	// Timer for delayed and timed actions
	private Timer timer;
	private int delay = 2000;

	//Some UI stuff
	Image img;
	MediaTracker tr;

	public MaxColorGame(java.applet.Applet app, Geometry world, boolean audioStatus)
	{
		this.app = app;
		this.world = world;
		this.audioOn = audioStatus;
		this.backgroundAudio = this.app.getAudioClip(this.app.getCodeBase(),
			"audio/background1.wav");

		tr = new MediaTracker(this.app);
		img = this.app.getImage(this.app.getCodeBase(), "images/score.png");
		tr.addImage(img,0);
	}

	public void initGame()
	{
		this.initLevel();
		if(this.audioOn)
			this.enableAudio();
		this.gameOn = true;
	}

	// Perform any necessary stop steps
	public void stop()
	{
		this.gameOn = false;

		// Make sure the timer stops at the stop of the game
		this.stopTimer();

		this.deleteCubeFromWorld();

		if(this.backgroundAudio != null)
		{
			this.backgroundAudio.stop();
			this.backgroundAudio = null;
		}
	}

	// Method to initialize a game level
	public void initLevel()
	{
		// If already a cube, clear it out
		if(this.cube != null)
		{
			this.deleteCubeFromWorld();
		}
		// Re-create the cube
		this.cube = new GameCube(world, this.numCubeDimensions);
		// Call method to randomize the cube tile colors
		this.cube.randomizeCubeColors();

		// Call method to start timer and related timed actions (for computer actions)
		this.startTimer();

		//this.gameOn = true;
	}

	private void initNextLevel()
	{
		this.level++;
		this.numCubeDimensions++;
		this.delay = this.delay-(this.delay/3);

		this.initLevel();
	}

	public void clickTile(int face, int row, int column)
	{
		if(this.gameOn)
		{
			Geometry[][] customGeoArray = this.cube.getFace(face);
			Material currentColor = customGeoArray[row][column].material;
			// Invert its color
			customGeoArray[row][column].setMaterial(currentColor == this.cube.getLitColor() ?
				this.cube.getUnlitColor() : this.cube.getLitColor());
			// Spread the tile color to the touching side tiles
			this.cube.setGeometryMaterial(customGeoArray[row-1][column], currentColor);
			this.cube.setGeometryMaterial(customGeoArray[row+1][column], currentColor);
			this.cube.setGeometryMaterial(customGeoArray[row][column-1], currentColor);
			this.cube.setGeometryMaterial(customGeoArray[row][column+1], currentColor);

			this.recalculateScore();
		}
	}

	private void recalculateScore()
	{
	}

	public void toggleAudio(boolean b)
	{
		if(b)
			enableAudio();
		else
			disableAudio();
	}

	public void enableAudio()
	{
		if(this.backgroundAudio == null)
		{
			this.backgroundAudio = this.app.getAudioClip(this.app.getCodeBase(),
				"audio/background1.wav");
		}

		this.backgroundAudio.loop();
	}

	public void disableAudio()
	{
		if (this.backgroundAudio != null) {
			this.backgroundAudio.stop();
		}
	}

	public void animate(double time)
	{
		if(this.cube != null)
		{
			this.cube.animate(time);
		}
	}

	public void drawOverlay(Graphics g)
	{
		// BELOW DRAW SCORE METER SHOULD BE PUSHED TO THE GAME INSTANCE
		// AS DIFFERENT GAMES WILL IMPLEMENT SCORING DIFFERENTLY
		g.drawImage(img, 420, 360, this.app);
		g.fillArc(420,360,90,90,0,this.getScore());
		g.drawString("SCORE METER",420,420);
	}

	void startTimer()
	{
		this.timer = new Timer(this.delay, this);
		this.timer.setInitialDelay(this.delay);
		this.timer.start();
	}

	void stopTimer()
	{
		if(this.timer != null)
		{
			this.timer.stop();
			this.timer = null;
		}
	}

	// Action to perform - ActionListener implementation
	public void actionPerformed(ActionEvent e)
	{
		//this.stopTimer();
		//this.initLevel();

		if(this.gameOn && !this.gameOver)
		{
			// When timer calls action, get a random tile and call its clicked action
			int face = (int)(Math.random()*this.cube.getNumFaces());
			int row = 1+(int)(Math.random()*this.cube.getDimension());
			int column = 1+(int)(Math.random()*this.cube.getDimension());
			this.clickTile(face, row, column);
		}
	}

	@Override
	public int getScore() {
		// TODO Auto-generated method stub
		if(this.cube != null)
			return this.cube.getScore();

		return 0;
	}
}