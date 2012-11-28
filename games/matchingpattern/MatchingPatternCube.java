package games.matchingpattern;

import elements.GameCube;
import render.Geometry;

public class MatchingPatternCube extends GameCube {

	public MatchingPatternCube(Geometry world, int dimension) {
		super(world, dimension);
	}

	public void animate(double time)
	{
		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				this.m = this.center.getMatrix();
				this.m.identity();
				
				// For each tile in the faces
				for(int row = 1; row <= this.dimension; row++)
				{
					for(int column = 1; column <= this.dimension; column++)
					{
						// Get its appropriate x and y coordinates, move and resize each tile
						stepX = -((double)(this.midDimension-column))/2*5/this.dimension;
						stepY = -((double)(this.midDimension-row))/2*5/this.dimension;

						// If the n-number of tiles is even, adjust the offset for no center tile
						if(this.dimension%2 == 0)
						{
							stepX += this.faceCenterOffset/this.dimension;
							stepY += this.faceCenterOffset/this.dimension;
						}

						// Set front face
						tempGeo = this.facesArray[0][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, stepY, this.faceCenterOffset);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set back face
						tempGeo = this.facesArray[1][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.rotateX(Math.PI);
						this.m.translate(stepX, stepY,this.faceCenterOffset);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);


						// Set top face
						tempGeo = this.facesArray[2][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, this.faceCenterOffset, stepY);
						this.m.rotateX(-Math.PI/2);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set bottom face
						tempGeo = this.facesArray[3][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, -this.faceCenterOffset, stepY);
						this.m.rotateX(Math.PI/2);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set right face
						tempGeo = this.facesArray[4][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(this.faceCenterOffset, stepX, stepY);
						this.m.rotateY(Math.PI/2);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set left face
						tempGeo = this.facesArray[5][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(-this.faceCenterOffset, stepX, stepY);
						this.m.rotateY(-Math.PI/2);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);
					}
				}
			}

		}
	}
	
	// Method to set a tile's material color
	public void showMeshOnFace(int face, int row, int col,String filename)
	{
		 this.facesArray[face][row][col].mesh(1,1, filename);
	}
	
	
		
}
