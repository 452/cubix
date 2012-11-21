package games.maxColorGame;

import games.*;
import elements.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.applet.AudioClip;

// Game to maximize player color on the game cube to win, vs computer
public class MaxColorGame extends Game
{
	java.applet.Applet app;

	// Cube variables
	Material playerMaterial, computerMaterial;
	Material[] materialArray = new Material[2];
	Color playerColor, computerColor;

	// Gameplay variables
	private int level = 1, maxLevel = 5, lives = 3, numCubeDimensions = 4;
	private double playerScore = 0, computerScore = 0, maxScore = 5000;
	private boolean gameOn = false, gameOver = false, gameWin = false;

	// Audio variables
	private AudioClip backgroundAudio;
	private boolean audioOn = false;

	// Timer for delayed and timed actions
	private Timer computerTimer, scoreTimer;
	private int computerDelay = 2000, minComputerDelay = 500, scoreDelay = 500;

	//Some UI stuff
	private int meterLength = 250, meterHeight = 15;

	// DEPRACATED METER VARIABLES
	//Image img;
	//MediaTracker tr;

	// Loaded constructor
	public MaxColorGame(java.applet.Applet app, Geometry world, boolean audioStatus)
	{
		// Set game variables
		this.app = app;
		this.world = world;
		this.audioOn = audioStatus;

		// Set cube and tile materials
		this.playerMaterial = new Material();
		this.playerMaterial.setAmbient(0.7, 0.7, 0.7);
		this.playerMaterial.setDiffuse(0.8, 0.8, 0.8);
		this.playerMaterial.setSpecular(0.9, 0.9, 0.9, 10);

		this.playerColor = new Color((int)(0.7*255), (int)(0.7*255), (int)(0.7*255));

		this.computerMaterial = new Material();
		this.computerMaterial.setAmbient(0.1, 0.1, 0.1);
		this.computerMaterial.setDiffuse(0.2, 0.2, 0.2);
		this.computerMaterial.setSpecular(.5, .5, .5, 10);

		this.computerColor = new Color((int)(0.1*255), (int)(0.1*255), (int)(0.1*255));

		this.materialArray[0] = this.playerMaterial;
		this.materialArray[1] = this.computerMaterial;

		// DEPRACATED METER VARIABLES
		//tr = new MediaTracker(this.app);
		//img = this.app.getImage(this.app.getCodeBase(), "images/score.png");
		//tr.addImage(img,0);
	}

	// Initialize the game
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

		// Make sure the timers stop at the stop of the game
		this.stopTimers();

		// Delete the 3D cube from the world
		this.deleteCubeFromWorld();

		// End any audio
		if(this.backgroundAudio != null)
		{
			this.backgroundAudio.stop();
			this.backgroundAudio = null;
		}
	}

	// Method to initialize a game level
	public void initLevel()
	{
		// Reset all game variables
		this.playerScore = this.computerScore = 0;

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
		this.startTimers();

		this.gameOn = true;
	}

	// Method to initialize to next level
	private void initNextLevel()
	{
		// Increment level
		this.level++;
		// Increase score to reach for a win
		this.maxScore += 200;
		// Increase number of n-tiles on cube face
		this.numCubeDimensions++;
		// Decrease the computer action delay, up to a minimum (Don't want computer action too fast)
		this.computerDelay = Math.max(this.computerDelay-(this.computerDelay/3), this.minComputerDelay);
		// Re-initialize the level with the new level parameters
		this.initLevel();
	}

	// Randomize this cube's tiles based on Material in materialArray
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
						this.cube.setTileMaterial(face, row, column, this.materialArray[(int)(Math.random()*2)]);
					}
				}
			}
		}
	}

	// Action to perform when a tile is clicked on (either by player or computer)
	public void clickTile(int face, int row, int column)
	{
		if(this.gameOn)
		{
			Geometry[][] customGeoArray = this.cube.getFace(face);
			Material currentColor = customGeoArray[row][column].material;
			// Invert its color
			customGeoArray[row][column].setMaterial(currentColor == this.playerMaterial ?
				this.computerMaterial : this.playerMaterial);
			// Spread the tile color to the touching side tiles
			this.cube.setTileMaterial(face, row-1, column, currentColor);
			this.cube.setTileMaterial(face, row+1, column, currentColor);
			this.cube.setTileMaterial(face, row, column-1, currentColor);
			this.cube.setTileMaterial(face, row, column+1, currentColor);
		}
	}

	// Method to perform the computer game action
	public void computerAction()
	{
		if(this.gameOn && !this.gameOver && this.cube != null)
		{
			int face, row, column;

			// Computer gets a random tile of its own color and call its clicked action
			do {
				face = (int)(Math.random()*this.cube.getNumFaces());
				row = 1+(int)(Math.random()*this.cube.getDimension());
				column = 1+(int)(Math.random()*this.cube.getDimension());
			}
			while(!this.computerMaterial.equals(this.cube.getFace(face)[row][column].getMaterial()));

			this.clickTile(face, row, column);
		}
	}

	private int tempPlayerScore, tempComputerScore;

	// Method to recalculate player and computer scores based on cube tile colors
	void recalculateScores()
	{
		this.tempPlayerScore = this.tempComputerScore = 0;

		if(this.cube != null)
		{
			for(int face = 0; face < this.cube.getNumFaces(); face++)
			{
				for(int row = 1; row <= this.cube.getDimension(); row++)
				{
					for(int column = 1; column <= this.cube.getDimension(); column++)
					{
						if(this.cube.getFace(face)[row][column] != null &&
							this.cube.getFace(face)[row][column].getMaterial() != null)
						{
							// Increment appropriate temp score based on tile color
							if(this.playerMaterial.equals(this.cube.getFace(face)[row][column].getMaterial()))
								++this.tempPlayerScore;
							else
								++this.tempComputerScore;
						}
					}
				}
			}
		}

		// Increase each score based on how much control it has over the cube
		this.playerScore += this.tempPlayerScore/this.level;
		this.computerScore += this.tempComputerScore/this.level;
	}

	// Method to check if player or computer reaches the level's max score
	public void checkForWin()
	{
		// If either score reaches the level's max score
		if(this.playerScore >= this.maxScore || this.computerScore >= this.maxScore)
		{
			// Stop active game and timers
			this.gameOn = false;
			this.stopTimers();

			// If the player won this level
			if(this.playerScore >= this.maxScore)
			{
				// Initialize to next level if there is one
				if(this.level < this.maxLevel)
				{
					this.initNextLevel();
				}
				// Otherwise the game is over and player wins
				else
				{
					this.gameOver = true;
					this.gameWin = true;
				}
			}
			// Else if the computer won this level
			else if(this.computerScore >= this.maxScore)
			{
				// Decrement player lives and if still alive, replay level
				if(--this.lives > 0)
				{
					this.initLevel();
				}
				// Else the game is over and player loses
				else
				{
					this.gameOver = true;
					this.gameWin = false;
				}
			}
		}
	}

	public void toggleAudio(boolean b)
	{
		if(b)
			enableAudio();
		else
			disableAudio();
	}

	// Enable and play audio
	public void enableAudio()
	{
		if(this.backgroundAudio == null)
		{
			this.backgroundAudio = this.app.getAudioClip(this.app.getCodeBase(),
				"audio/background1.wav");
		}

		this.backgroundAudio.loop();
	}

	// Disable and stop audio
	public void disableAudio()
	{
		if (this.backgroundAudio != null) {
			this.backgroundAudio.stop();
		}
	}

	// Animate the cube
	public void animate(double time)
	{
		if(this.cube != null)
		{
			this.cube.animate(time);
		}
	}

	private Font tempFont;
	private Color tempColor;

	// Draw any game specific elements
	public void drawOverlay(Graphics g)
	{
		// Store font and color already in the system
		this.tempColor = g.getColor();
		this.tempFont = g.getFont();

		// If game is over, draw the message
		if(this.gameOver)
		{
			g.setColor(Color.RED);
			g.setFont(Fonts.BIG_FONT);
			g.drawString("GAME OVER", 220, 200);
			g.drawString((this.gameWin ? "YOU WIN!" : "YOU LOSE!"), 240, 250);
		}

		// Draw lives and level information
		g.setColor(Color.BLUE);
		g.setFont(Fonts.BIG_FONT);
		g.drawString(""+this.lives, 30, 180);
		g.drawString(""+this.level, 30, 330);

		g.setFont(Fonts.SMALL_FONT);
		g.drawString("LIVES:", 10, 130);
		g.drawString("LEVEL:", 10, 280);

		// Draw the top level text
		g.drawString("COLOR MAXIMUS", 230, 30);

		g.setFont(Fonts.TINY_FONT);
		g.drawString("Click on a tile to migrate the color to its connecting tiles", 150, 45);
		g.drawString("The more tiles a color possesses, the faster its meter fills", 140, 60);
		g.drawString("The first color to fill its meter wins the level", 190, 75);

		// Draw player and computer meter information
		g.setColor(Color.BLACK);
		g.drawString("PLAYER:", 120, 440);
		g.drawRect(200, 425, this.meterLength, this.meterHeight);
		g.setColor(this.playerColor);
		g.fillRect(200, 425, (int)((this.playerScore/this.maxScore)*this.meterLength), this.meterHeight);

		g.setColor(Color.BLACK);
		g.drawString("COMPUTER:", 120, 460);
		g.drawRect(200, 445, this.meterLength, this.meterHeight);
		g.setColor(this.computerColor);
		g.fillRect(200, 445, (int)((this.computerScore/this.maxScore)*this.meterLength), this.meterHeight);

		g.setFont(tempFont);
		g.setColor(tempColor);

		// DEPRACATED
		// Draw score meter
		//g.drawImage(img, 520, 400, this.app);
		//g.fillArc(520,400,90,90,0,this.getScore());
		//g.drawString("SCORE METER",520,460);
	}

	// Start the timers
	void startTimers()
	{
		// Inner ScoreListener class is to check the scores and for a win
		class ScoreListener implements ActionListener
		{
			MaxColorGame game;

			public ScoreListener(MaxColorGame game)
			{
				this.game = game;
			}

			public void actionPerformed(ActionEvent e)
			{
				if(this.game != null)
				{
					this.game.recalculateScores();
					this.game.checkForWin();
				}
			}
		}
		this.scoreTimer = new Timer(this.scoreDelay, new ScoreListener(this));
		this.scoreTimer.setInitialDelay(this.scoreDelay);
		this.scoreTimer.start();

		// Inner ComputerActionListener class is for computer play
		class ComputerActionListener implements ActionListener
		{
			MaxColorGame game;

			public ComputerActionListener(MaxColorGame game)
			{
				this.game = game;
			}

			public void actionPerformed(ActionEvent e)
			{
				if(this.game != null)
				{
					this.game.computerAction();
				}
			}
		}
		this.computerTimer = new Timer(this.computerDelay, new ComputerActionListener(this));
		this.computerTimer.setInitialDelay(this.computerDelay);
		this.computerTimer.start();
	}

	// Method to stop and nullify all timers
	void stopTimers()
	{
		if(this.scoreTimer != null)
		{
			this.scoreTimer.stop();
			this.scoreTimer = null;
		}
		if(this.computerTimer != null)
		{
			this.computerTimer.stop();
			this.computerTimer = null;
		}
	}

	// DEPRACATED FOR ARC METER
	/*
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
							this.playerMaterial.equals(this.cube.getFace(face)[row][column].getMaterial()))
						{
							score++;
						}
					}
				}
			}
		}

		return score;
	}
	*/
}