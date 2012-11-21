package games.matchingGame;

import games.*;
import elements.*;
import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.applet.AudioClip;

public class MatchingGame extends Game implements ActionListener
{
	java.applet.Applet app;

	private int level = 1, numCubeDimensions = 3;

	// Gameplay variables
	private boolean gameOn = false, gameOver = false, gameWin = false;

	// Audio variables
	private AudioClip backgroundAudio;
	private boolean audioOn = false;

	public MatchingGame(java.applet.Applet app, Geometry world, boolean audioStatus)
	{
		this.app = app;
		this.world = world;
		this.audioOn = audioStatus;
		this.backgroundAudio = this.app.getAudioClip(this.app.getCodeBase(),
			"audio/background1.wav");
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
		this.cube = new MatchingCube(world, this.numCubeDimensions);
		// Call method to randomize the cube tile colors
		((MatchingCube)(this.cube)).randomizeCubeColors();
	}

	public void clickTile(int face, int row, int column)
	{
		((MatchingCube)(this.cube)).actionAt(face, row, column);
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
	}

	// Action to perform - ActionListener implementation
	public void actionPerformed(ActionEvent e)
	{
	}
}