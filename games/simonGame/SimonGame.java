package games.simonGame;

import games.*;
import elements.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.applet.AudioClip;

// A 3D Simon Game
public class SimonGame extends Game
{
	java.applet.Applet app;

	// Simon game variables
	private static final int NUM_FACES = 6;
	static final Color[] COLOR_ARRAY = {
		new Color((int)(255*0.7),0,0), new Color(0,(int)(255*0.7),0),
		new Color(0,0,(int)(255*0.7)), new Color((int)(255*0.7),(int)(255*0.7),0),
		new Color(0,(int)(255*0.7),(int)(255*0.7)), new Color((int)(255*0.7),0,(int)(255*0.7))
	};
	static final Color[] DIFFUSE_COLOR_ARRAY = {
		new Color((int)(255*0.8),0,0), new Color(0,(int)(255*0.8),0),
		new Color(0,0,(int)(255*0.8)), new Color((int)(255*0.8),(int)(255*0.8),0),
		new Color(0,(int)(255*0.8),(int)(255*0.8)), new Color((int)(255*0.8),0,(int)(255*0.8))
	};

	// Cube variables
	static Material[] materialArray = new Material[NUM_FACES];

	// Gameplay variables
	private int level = 1, lives = 3, incrementNum = 3;
	private int maxPlayCount = 15, currentPlayCount = 15;
	private double playerScore = 0;
	private boolean gameOn = false, gameOver = false, gameWin = false;
	private boolean flashOn = false, userEnabled = false;

	// Simon arrays
	private int max = 10, repeatIndex = 2, currentIndex = -1;
	private int polygonDimension = 30, step = 10;
	private Polygon[] polygonArray = new Polygon[max];
	private int[] repeatArray = new int[max];

	// Audio variables
	private AudioClip backgroundAudio, buzzerAudio, dingAudio;
	private boolean audioOn = false;
	private MusicKeyboard keyboard;
	int key = 4; // no-go: 6, 7, 8, 9, 10, 11, 12
	private int[] notesArray = { 0, 1, 2, 3, 4, 5 };

	// Timer for delayed and timed actions
	private Timer repeatTimer, countDownTimer;
	private int delay = 1000, flashDelay = 2000;

	// Loaded constructor
	public SimonGame(java.applet.Applet app, Geometry world, boolean audioStatus)
	{
		// Set game variables
		this.app = app;
		this.world = world;

		// Set audio variables
		this.audioOn = audioStatus;
		this.buzzerAudio = this.app.getAudioClip(this.app.getCodeBase(),
			"audio/buzzer.wav");
		this.dingAudio = this.app.getAudioClip(this.app.getCodeBase(),
			"audio/ding.wav");
		this.keyboard = new MusicKeyboard(this.notesArray);

		// Set the polygon arrays
		for(int i = 0; i < this.polygonArray.length; i++)
		{
			Polygon p = new Polygon();
			p.addPoint(130+this.polygonDimension*i+step*i, 430);
			p.addPoint(130+this.polygonDimension*i+step*i+this.polygonDimension, 430);
			p.addPoint(130+this.polygonDimension*i+step*i+this.polygonDimension, 430+this.polygonDimension);
			p.addPoint(130+this.polygonDimension*i+step*i, 430+this.polygonDimension);
			this.polygonArray[i] = p;
		}

		// Set the color arrays
		for(int i = 0; i < this.max; i++)
		{
			this.repeatArray[i] = i%SimonGame.COLOR_ARRAY.length;
		}

		// Set the material array values
		for(int i = 0; i < this.materialArray.length; i++)
		{
			Material tempMaterial = new Material();
			tempMaterial.setAmbient(
				(double)SimonGame.COLOR_ARRAY[i].getRed()/255,
				(double)SimonGame.COLOR_ARRAY[i].getGreen()/255,
				(double)SimonGame.COLOR_ARRAY[i].getBlue()/255);
			tempMaterial.setDiffuse(
				Math.min((double)SimonGame.COLOR_ARRAY[i].getRed()/255+0.1, 1),
				Math.min((double)SimonGame.COLOR_ARRAY[i].getGreen()/255+0.1, 1),
				Math.min((double)SimonGame.COLOR_ARRAY[i].getBlue()/255+0.1, 1));
			tempMaterial.setSpecular(
				Math.min((double)SimonGame.COLOR_ARRAY[i].getRed()/255+0.2, 1),
				Math.min((double)SimonGame.COLOR_ARRAY[i].getGreen()/255+0.2, 1),
				Math.min((double)SimonGame.COLOR_ARRAY[i].getBlue()/255+0.2, 1),
				10);
			//tempMaterial.setTransparency(0.25);
			SimonGame.materialArray[i] = tempMaterial;
		}

		this.cube = new SimonCube(this.world);
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
		if(this.buzzerAudio != null)
		{
			this.buzzerAudio.stop();
			this.buzzerAudio = null;
		}
		if(this.dingAudio != null)
		{
			this.dingAudio.stop();
			this.dingAudio = null;
		}
		if(this.keyboard != null)
		{
			this.keyboard.stop();
		}
	}

	// Method to initialize a game level
	public void initLevel()
	{
		// Reset all game variables
		this.lives = 3;
		this.currentPlayCount = this.maxPlayCount;
		this.currentIndex = -1;

		// Call the method to set the random colored tones to repeat
		this.initColoredTones();

		// Call method to start timer to flash all colored tones to repeat
		this.currentFlash = -1;
		this.flashOn = true;
		this.startRepeatTimer();

		this.gameOn = true;
	}

	// Method to initialize to next level
	private void initNextLevel()
	{
		// Increment level
		this.level++;

		// Increase the index of number to repeat within a certain bound
		if(this.level % this.incrementNum == 0)
		{
			this.repeatIndex = Math.min(this.max, this.repeatIndex+1);
		}

		// Re-initialize the level with the new level parameters
		this.initLevel();
	}

	private void initColoredTones()
	{
		for(int i = 0; i <= this.repeatIndex; i++)
		{
			this.repeatArray[i] = (int)(Math.random()*SimonGame.NUM_FACES);
		}
	}

	// Action to perform when a tile is clicked on (either by player or computer)
	public void clickTile(int face, int row, int column)
	{
		if(this.gameOn && this.userEnabled)
		{((SimonCube)this.cube).setFaceHit(face);
			if(face == this.repeatArray[this.currentIndex+1])
			{
				//this.dingAudio.play();
				this.keyboard.addNote(this.notesArray[this.repeatArray[this.currentIndex+1]]);

				if(++this.currentIndex == this.repeatIndex)
				{
					this.userEnabled = false;
					this.countDownTimer.stop();
					this.initNextLevel();
				}
			}
			else
			{
				this.buzzerAudio.play();
				if(--this.lives == 0)
				{
					this.userEnabled = false;
					this.countDownTimer.stop();
					this.initLevel();
				}
			}
		}
	}

	public void checkForWin()
	{

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
				"audio/background3-low.wav");
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
		this.keyboard.playNotes();

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
		if(this.userEnabled)
			g.drawString(""+this.currentPlayCount, 560, 180);

		g.setFont(Fonts.SMALL_FONT);
		g.drawString("LIVES:", 10, 130);
		g.drawString("LEVEL:", 10, 280);
		g.drawString("TIMER:", 550, 130);

		// Draw the top level text
		g.drawString("SIMON 3-DIMENSIONUS", 210, 30);

		g.setFont(Fonts.TINY_FONT);
		g.drawString("A 3D version of the classic Simon memory game", 180, 45);
		g.drawString("Repeat the colored tones given at the start of each level", 155, 60);

		// FILL ALL REPEAT POLYGONS
		if(this.flashOn)
		{
			for(int i = 0; i < this.currentFlash; i++)
			{
				g.setColor(SimonGame.DIFFUSE_COLOR_ARRAY[this.repeatArray[i]]);
				g.fillPolygon(this.polygonArray[i]);
			}
		}
		if(this.userEnabled)
		{
			for(int i = 0; i <= currentIndex; i++)
			{
				g.setColor(SimonGame.DIFFUSE_COLOR_ARRAY[this.repeatArray[i]]);
				g.fillPolygon(this.polygonArray[i]);
			}
		}

		g.setFont(tempFont);
		g.setColor(tempColor);
	}

	void startRepeatTimer()
	{
		// Inner repeat timer
		class RepeatListener implements ActionListener
		{
			SimonGame game;

			public RepeatListener(SimonGame game)
			{
				this.game = game;
			}

			public void actionPerformed(ActionEvent e)
			{
				if(this.game != null)
				{
					this.game.flashNextRepeat();
				}
			}
		}

		if(this.repeatTimer == null)
		{
			this.repeatTimer = new Timer(this.flashDelay, new RepeatListener(this));
			this.repeatTimer.setInitialDelay(this.delay);
		}
		this.repeatTimer.start();
	}

	int currentFlash = -1;
	private void flashNextRepeat()
	{
		if(this.currentFlash > this.repeatIndex)
		{
			this.repeatTimer.stop();
			this.flashOn = false;
			this.userEnabled = true;
			this.startCountDownTimer();
		}
		else
		{
			if(this.currentFlash >= 0)
				this.keyboard.addNote(this.notesArray[this.repeatArray[this.currentFlash]]);
			++this.currentFlash;
		}
	}

	private void startCountDownTimer()
	{
		// Inner countdown timer
		class CountDownListener implements ActionListener
		{
			SimonGame game;

			public CountDownListener(SimonGame game)
			{
				this.game = game;
			}

			public void actionPerformed(ActionEvent e)
			{
				if(this.game != null)
				{
					this.game.decrementPlayCount();
				}
			}
		}

		if(this.countDownTimer == null)
		{
			this.countDownTimer = new Timer(this.delay, new CountDownListener(this));
			this.countDownTimer.setInitialDelay(this.delay);
		}
		this.countDownTimer.start();
	}

	private void decrementPlayCount()
	{
		if(--this.currentPlayCount == 0)
		{
			this.countDownTimer.stop();
			this.userEnabled = false;
			this.initLevel();
		}
	}

	// Method to stop and nullify all timers
	void stopTimers()
	{
		if(this.repeatTimer != null)
		{
			this.repeatTimer.stop();
			this.repeatTimer = null;
		}
		if(this.countDownTimer != null)
		{
			this.countDownTimer.stop();
			this.countDownTimer = null;
		}
	}
}