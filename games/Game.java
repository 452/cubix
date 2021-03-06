package games;

import elements.*;
import render.*;

import java.awt.*;

public abstract class Game
{
	public Geometry world;
	public elements.GameCube cube;

	public void deleteCubeFromWorld()
	{
		if(this.world != null && this.cube != null)
		{
			this.cube.removeFromWorld(this.world);
			this.cube = null;
		}
	}

	public abstract void initGame();
	public abstract void initLevel();
	public abstract void stop();
	public abstract void clickTile(int face, int row, int column);
	public abstract void toggleAudio(boolean b);
	public abstract void enableAudio();
	public abstract void disableAudio();
	public abstract void animate(double time);
	public abstract void drawOverlay(Graphics g);
}