package games;

import elements.*;
import render.*;
import java.awt.*;

public abstract class Game
{
	Geometry world;
	elements.GameCube cube;

	public void deleteCubeFromWorld()
	{
		if(this.world != null && this.cube != null)
		{
			this.cube.removeFromWorld(world);
			this.cube = null;
		}
	}

	public abstract void initGame();
	public abstract void initLevel();
	public abstract void stop();
	public abstract void clickTile(int face, int row, int column);
	public abstract void animate(double time);
	public abstract void drawOverlay(Graphics g);
}