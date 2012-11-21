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

	// Cube variables
	Material litColor, unlitColor;
	Material[] colorArray = new Material[2];

	// Gameplay variables
	private int level = 1, numCubeDimensions = 4;
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

		// Set cube and tile materials
		this.litColor = new Material();
		this.litColor.setAmbient(0.7, 0.7, 0.7);
		this.litColor.setDiffuse(0.8, 0.8, 0.8);
		this.litColor.setSpecular(0.9, 0.9, 0.9, 10);

		this.unlitColor = new Material();
		this.unlitColor.setAmbient(0.1, 0.1, 0.1);
		this.unlitColor.setDiffuse(0.2, 0.2, 0.2);
		this.unlitColor.setSpecular(.5, .5, .5, 10);

		this.colorArray[0] = this.litColor;
		this.colorArray[1] = this.unlitColor;

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
		this.randomizeCubeColors();

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



	// Randomize this cube's tiles based on Material in colorArray
	public void randomizeCubeColors()
	{
		if(this.cube != null)
		{
			for(int face = 0; face < this.cube.getNumFaces(); face++)
			{
				for(int row = 1; row <= this.cube.getDimension(); row++)
				{
					for(int column = 1; column <= this.cube.getDimension(); column++)
					{
						this.cube.setTileMaterial(face, row, column, this.colorArray[(int)(Math.random()*2)]);
					}
				}
			}
		}
	}

	public void clickTile(int face, int row, int column)
	{
		if(this.gameOn)
		{
			Geometry[][] customGeoArray = this.cube.getFace(face);
			Material currentColor = customGeoArray[row][column].material;
			// Invert its color
			customGeoArray[row][column].setMaterial(currentColor == this.litColor ?
				this.unlitColor : this.litColor);
			// Spread the tile color to the touching side tiles
			this.cube.setTileMaterial(face, row-1, column, currentColor);
			this.cube.setTileMaterial(face, row+1, column, currentColor);
			this.cube.setTileMaterial(face, row, column-1, currentColor);
			this.cube.setTileMaterial(face, row, column+1, currentColor);

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
		// Draw score meter
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
		int score = 0;

		if(this.cube != null)
		{
			for(int face = 0; face < this.cube.getNumFaces(); face++)
			{
				for(int row = 1; row <= this.cube.getDimension(); row++)
				{
					for(int column = 1; column <= this.cube.getDimension(); column++)
					{
						if(this.cube.getFace(face)[row][column] != null &&
							this.litColor.equals(this.cube.getFace(face)[row][column].getMaterial()))
						{
							score++;
						}
					}
				}
			}
		}

		return score;
	}
}