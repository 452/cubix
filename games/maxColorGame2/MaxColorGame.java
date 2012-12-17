package games.maxColorGame2;

import games.*;
import elements.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.applet.AudioClip;
import java.util.HashMap;

// Game to maximize player color on the game cube to win, vs computer
public class MaxColorGame extends Game
{
	java.applet.Applet app;

	// Cube variables
	Material playerMaterial0, playerMaterial1, computerMaterial0, computerMaterial1;
	HashMap<Integer, Material[]> materialMap = new HashMap<Integer, Material[]>();
	//Material[] materialArray = new Material[2];
	Color playerColor, computerColor;

	// Gameplay variables
	private static int PLAYER = 0, COMPUTER = 1;
	private int[][][] playerIntArray;
	private int level = 1, maxLevel = 5, lives = 3, numCubeDimensions = 4;
	private double playerScore = 0, computerScore = 0, maxScore = 500;
	private boolean gameOn = false, gameOver = false, gameWin = false;

	// Audio variables
	private AudioClip backgroundAudio;
	private boolean audioOn = false;

	// Timer for delayed and timed actions
	private Timer computerTimer, scoreTimer;
	private int computerDelay = 2000, minComputerDelay = 350, scoreDelay = 500;

	//Some UI stuff
	private int meterLength = 250, meterHeight = 15;

	// Loaded constructor
	public MaxColorGame(java.applet.Applet app, Geometry world, boolean audioStatus)
	{
		// Set game variables
		this.app = app;
		this.world = world;
		this.audioOn = audioStatus;

		this.playerColor = new Color((int)(0.7*255), (int)(0.7*255), (int)(0.7*255));
		this.computerColor = new Color((int)(0.1*255), (int)(0.1*255), (int)(0.1*255));

		// Set cube and tile materials
		this.playerMaterial0 = new Material();
		this.playerMaterial0.setAmbient(0.3, 1.0, 0.3);
		this.playerMaterial0.setDiffuse(0.3, 1.0, 0.3);
		this.playerMaterial0.setSpecular(0.3, 1.0, 0.3, 10);

		this.computerMaterial0 = new Material();
		this.computerMaterial0.setAmbient(0.1, 1.0, 0.1);
		this.computerMaterial0.setDiffuse(0.15, 1.0, 0.15);
		this.computerMaterial0.setSpecular(.2, 1.0, .2, 10);

		Material[] tempMaterialArray0 = { this.playerMaterial0, this.computerMaterial0 };
		this.materialMap.put(PLAYER, tempMaterialArray0);

		this.playerMaterial1 = new Material();
		this.playerMaterial1.setAmbient(0.7, 0.7, 0.7);
		this.playerMaterial1.setDiffuse(0.8, 0.8, 0.8);
		this.playerMaterial1.setSpecular(0.9, 0.9, 0.9, 10);

		this.computerMaterial1 = new Material();
		this.computerMaterial1.setAmbient(0.1, 0.1, 0.1);
		this.computerMaterial1.setDiffuse(0.2, 0.2, 0.2);
		this.computerMaterial1.setSpecular(.5, .5, .5, 10);

		Material[] tempMaterialArray1 = { this.playerMaterial1, this.computerMaterial1 };
		this.materialMap.put(COMPUTER, tempMaterialArray1);
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
		// Re-initialize the player int array for the cube
		this.playerIntArray = new int[this.cube.getNumFaces()]
			[this.cube.getDimension()+2][this.cube.getDimension()+2];
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
		this.maxScore += 50;
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
				// Reset the temp player tiles
				this.tempPlayerTiles = this.tempComputerTiles = 0;

				for(int row = 1; row <= this.cube.getDimension(); row++)
				{
					for(int column = 1; column <= this.cube.getDimension(); column++)
					{
						int temp = (int)(Math.random()*2);
						this.setPlayerIntArray(face, row, column, temp);
						if(temp == PLAYER)
						{
							++this.tempPlayerTiles;
						}
						else
						{
							++this.tempComputerTiles;
						}
						//this.cube.setTileMaterial(face, row, column, this.materialArray[(int)(Math.random()*2)]);
					}
				}

				if(this.tempPlayerTiles >= this.tempComputerTiles)
				{
					this.tempMaterialArray = this.materialMap.get(PLAYER);

					for(int row = 1; row <= this.cube.getDimension(); row++)
					{
						for(int column = 1; column <= this.cube.getDimension(); column++)
						{
							this.cube.setTileMaterial(face, row, column,
								this.tempMaterialArray[this.getPlayerIntArray(
								face, row, column)]);
						}
					}
				}
				else
				{
					this.tempMaterialArray = this.materialMap.get(COMPUTER);

					for(int row = 1; row <= this.cube.getDimension(); row++)
					{
						for(int column = 1; column <= this.cube.getDimension(); column++)
						{
							this.cube.setTileMaterial(face, row, column,
								this.tempMaterialArray[this.getPlayerIntArray(
								face, row, column)]);
						}
					}
				}
			}
		}
	}

	private void setPlayerIntArray(int face, int row, int column, int temp)
	{
		this.playerIntArray[face][row][column] = temp;
	}

	private int getPlayerIntArray(int face, int row, int column)
	{
		return this.playerIntArray[face][row][column];
	}

	private boolean[] facesToUpdate = new boolean[6];

	private Geometry tempTile;
	private Material[] tempMaterialArray;
	// Action to perform when a tile is clicked on (either by player or computer)
	public void clickTile(int face, int row, int column)
	{
		if(this.gameOn)
		{
			for(int i = 0; i < this.facesToUpdate.length; i++)
			{
				this.facesToUpdate[i] = false;
			}

			// Get current player int value of clicked tile
			int currentI = this.getPlayerIntArray(face, row, column);
			int invertedI = (currentI+1)%2;
			// Invert the tile clicked
			this.setPlayerIntArray(face, row, column, invertedI);
			// Set the current face to update
			this.facesToUpdate[face] = true;

			// Spread the tile color to the touching side tiles
			this.tempTile = this.cube.getFace(face)[row-1][column];
			this.setPlayerIntArray(
				this.tempTile.getFace(),
				this.tempTile.getPositionRow(),
				this.tempTile.getPositionColumn(),
				currentI);
			this.facesToUpdate[this.tempTile.getFace()] = true;
			this.tempTile = this.cube.getFace(face)[row+1][column];
			this.setPlayerIntArray(
				this.tempTile.getFace(),
				this.tempTile.getPositionRow(),
				this.tempTile.getPositionColumn(),
				currentI);
			this.facesToUpdate[this.tempTile.getFace()] = true;
			this.tempTile = this.cube.getFace(face)[row][column-1];
			this.setPlayerIntArray(
				this.tempTile.getFace(),
				this.tempTile.getPositionRow(),
				this.tempTile.getPositionColumn(),
				currentI);
			this.facesToUpdate[this.tempTile.getFace()] = true;
			this.tempTile = this.cube.getFace(face)[row][column+1];
			this.setPlayerIntArray(
				this.tempTile.getFace(),
				this.tempTile.getPositionRow(),
				this.tempTile.getPositionColumn(),
				currentI);
			this.facesToUpdate[this.tempTile.getFace()] = true;

			// Update each face that needs it with the controlling player
			for(int f = 0; f < this.facesToUpdate.length; f++)
			{
				if(this.facesToUpdate[f] == true)
				{
					int controller = getFaceController(f);
					this.tempMaterialArray = this.materialMap.get(controller);

					for(int r = 1; r <= this.cube.getDimension(); r++)
					{
						for(int c = 1; c <= this.cube.getDimension(); c++)
						{
							this.cube.setTileMaterial(f, r, c,
								this.tempMaterialArray[this.getPlayerIntArray(f, r, c)]);
						}
					}
				}
			}
		}
	}

	private int getFaceController(int face)
	{
		// Reset the number of tiles each player controls
		this.tempPlayerTiles = this.tempComputerTiles = 0;

		if(this.cube != null)
		{
			for(int row = 1; row <= this.cube.getDimension(); row++)
			{
				for(int column = 1; column <= this.cube.getDimension(); column++)
				{
					if(this.getPlayerIntArray(face, row, column) == PLAYER)
					{
						++this.tempPlayerTiles;
					}
					else
					{
						++this.tempComputerTiles;
					}
				}
			}
			if(this.tempPlayerTiles >= this.tempComputerTiles)
			{
				return PLAYER;
			}
			else
			{
				return COMPUTER;
			}
		}
		return -1;
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
			while(this.getPlayerIntArray(face, row, column) != COMPUTER);

			this.clickTile(face, row, column);
		}
	}

	private int tempPlayerScore, tempComputerScore;
	private int tempPlayerTiles, tempComputerTiles;

	// Method to recalculate player and computer scores based on cube tile colors
	void recalculateScores()
	{
		this.tempPlayerScore = this.tempComputerScore = 0;

		if(this.cube != null)
		{
			for(int face = 0; face < this.cube.getNumFaces(); face++)
			{
				int controller = getFaceController(face);
				if(controller == PLAYER)
				{
					++this.tempPlayerScore;
				}
				else
				{
					++this.tempComputerScore;
				}
			}
		}

		// Increase each score based on how much control it has over the cube
		this.playerScore += this.tempPlayerScore;///this.level;
		this.computerScore += this.tempComputerScore;///this.level;
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
		g.drawString("COLOR MAXIMUS V2", 220, 30);

		g.setFont(Fonts.TINY_FONT);
		g.drawString("Click on a tile to migrate the color to its connecting tiles", 150, 45);
		g.drawString("The color with the most tiles controls that face", 170, 60);
		g.drawString("The more faces a color controls, the faster its meter fills", 145, 75);
		g.drawString("The first color to fill its meter wins the level", 190, 90);

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
}