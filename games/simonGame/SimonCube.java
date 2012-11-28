package games.simonGame;

import elements.*;
import render.*;

public class SimonCube extends GameCube
{
	public SimonCube(Geometry world)
	{
		super(world, 1);
		for(int face = 0; face < this.numFaces; face++)
		{
			// Only one tile per face for Simon, but doesn't hurt iterating anyway
			for(int i = 1; i <= this.dimension; i++)
			{
				this.facesArray[face][i][i].setMaterial(SimonGame.materialArray[face]);
			}
		}
	}
}