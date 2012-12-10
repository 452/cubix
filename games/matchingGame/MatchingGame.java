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

	int effectTime1 = 0;
	int effectTime2 = 0;
	int timer = 0;
	// Gameplay variables
	private boolean gameOn = false, gameOver = false, gameWin = false;

	private MatchingCube temp;
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
		if(this.level == 1){
			this.cube = new MatchingCube(world, this.numCubeDimensions);
		}
		else{
			this.cube = new MatchingCube(world, this.numCubeDimensions + 1);
		}
		// Call method to randomize the cube tile colors
		((MatchingCube)(this.cube)).randomizeCubeColors();
	}

	public void clickTile(int face, int row, int column)
	{
		if(this.gameWin){
			this.initLevel();
			this.gameWin = false;
			this.gameOver = false;
		}
		else{	
			boolean isWin = ((MatchingCube)(this.cube)).actionAt(face, row, column);	
			if(isWin){
				this.gameOver = true;
				this.gameWin = true;
				if(this.level == 1)
					this.level = 2;
				else
					this.level = 1;
			}
		}
		if(((MatchingCube)(this.cube)).clickTime % 2 == 1){
			this.effectTime1 = 0;
		}
		else{
			this.effectTime2 = 0;
		}
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

	public void rectEffect1(Graphics g){
		g.fillRect((int)(120 - 60 * Math.sin((double)effectTime1 * Math.PI / 180)), (int)(140 - 60 * Math.sin((double)effectTime1 * Math.PI / 180))
				, (int)(120 * Math.sin((double)effectTime1 * Math.PI / 180)), (int)(120 * Math.sin((double)effectTime1 * Math.PI / 180)));
		if(this.effectTime1 != 120){
			this.effectTime1 +=10;
		}
	}
	
	public void rectEffect2(Graphics g){
		g.fillRect((int)(120 - 60 * Math.sin((double)effectTime2 * Math.PI / 180)), (int)(280 - 60 * Math.sin((double)effectTime2 * Math.PI / 180))
				, (int)(120 * Math.sin((double)effectTime2 * Math.PI / 180)), (int)(120 * Math.sin((double)effectTime2 * Math.PI / 180)));
		if(this.effectTime2 != 120){
			this.effectTime2 +=10;
		}
	}
	
	public void drawOverlay(Graphics g)
	{
		if(this.gameOver)
		{
			g.setColor(Color.RED);
			g.setFont(Fonts.BIG_FONT);
			g.drawString((this.gameWin ? "YOU WIN!" : "YOU LOSE!"), 240, 250);
		}
		
		g.setColor(Color.BLUE);
		g.setFont(Fonts.BIG_FONT);
		g.drawString("Level " + this.level, 30, 400);
		
		this.temp = (MatchingCube)this.cube;
		String s = "Find the matching color...";
		if(this.temp.getOneColor() != null){
			g.setColor(this.temp.getOneColor());
			//g.fillRect(30, 200, (int)(100 * Math.sin(Math.PI / 2)), (int)(100 * Math.sin(Math.PI / 2)));//center at 70, 240
			this.rectEffect1(g);
		}
		if(this.temp.getTwoColor() != null){
			g.setColor(this.temp.getTwoColor());
			//g.fillRect(550, 200, (int)(100 * Math.sin(Math.PI / 2)), (int)(100 * Math.sin(Math.PI / 2)));// center at 590, 240
			this.rectEffect2(g);
			if(this.temp.getOneColor().equals(this.temp.getTwoColor())){
				s = "Great! Keep on finding!";
			}
			else{
				s = "Memorize the colors!";
			}
		}
		g.setColor(Color.BLUE);
		g.setFont(Fonts.SMALL_FONT);
		g.drawString(s, 200, 80);
	}

	// Action to perform - ActionListener implementation
	public void actionPerformed(ActionEvent e)
	{
	}
}