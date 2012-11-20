package elements;

import java.awt.Color;
import java.awt.Graphics;

import render.*;

public class GameCube
{
	Material litColor, unlitColor;
	Material[] colorArray = new Material[2];
	int dimension, 	midDimension;
	Geometry center;
	Matrix m;
	// 3D array of tiles, faces order as follows: front, back, top, bottom, right, left
	Geometry[][][] facesArray;
	double tileScaleDimension, stepX, stepY;
	// Below variables should never change
	private final double tileThickness = 0.01, faceCenterOffset = 1.25;
	private final int numFaces = 6;

	// Variables to handle concurrency issues
	private boolean isActive = false;
	private final Object LOCK = new Object();

	public GameCube(Geometry world, int dimension)
	{
		synchronized(this.LOCK)
		{
			this.center = world.add();
			this.initialize(dimension);
			this.isActive = true;
		}
	}

	void initialize(int d)
	{
		// Ensure number of input dimensions > 0, else default 1
		if(d > 0)
		{
			this.dimension = d;
			this.midDimension = d/2+1;
		}
		else
		{
			this.dimension = this.midDimension = 1;
		}

		// Initialize appropriate cube dimensions
		this.facesArray = new Geometry[this.numFaces][this.dimension+2][this.dimension+2];
		this.tileScaleDimension = 1.25/this.dimension;

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

		// Initialize all tiles for each face of the 3D cube
		for(int face = 0; face < this.numFaces; face++)
		{
			for(int row = 1; row <= this.dimension; row++)
			{
				for(int column = 1; column <= this.dimension; column++)
				{
					Geometry tempGeo = this.center.add().cube();
					tempGeo.setMaterial(this.colorArray[0]);
					tempGeo.setFace(face);
					tempGeo.setPositionRow(row);
					tempGeo.setPositionColumn(column);
					this.facesArray[face][row][column] = tempGeo;
				}
			}
		}

		// Set the connecting tiles of each face edge
		// Faces index order as follows: 0=front, 1=back, 2=top, 3=bottom, 4=right, 5=left
		for(int dim = 1; dim <= this.dimension; dim++)
		{
			// Set connecting tiles of front face edges
			this.facesArray[0][this.dimension+1][dim] = this.facesArray[2][this.dimension][dim];
			this.facesArray[0][0][dim] = this.facesArray[3][this.dimension][dim];
			this.facesArray[0][dim][0] = this.facesArray[5][this.dimension][dim];
			this.facesArray[0][dim][this.dimension+1] = this.facesArray[4][this.dimension][dim];

			// Set connecting tiles of back face edges
			this.facesArray[1][this.dimension+1][dim] = this.facesArray[2][1][dim];
			this.facesArray[1][0][dim] = this.facesArray[3][1][dim];
			this.facesArray[1][dim][this.dimension+1] = this.facesArray[4][1][dim];
			this.facesArray[1][dim][0] = this.facesArray[5][1][dim];

			// Set connecting tiles of top face edges
			this.facesArray[2][this.dimension+1][dim] = this.facesArray[0][this.dimension][dim];
			this.facesArray[2][0][dim] = this.facesArray[1][this.dimension][dim];
			this.facesArray[2][dim][0] = this.facesArray[5][dim][this.dimension];
			this.facesArray[2][dim][this.dimension+1] = this.facesArray[4][dim][this.dimension];

			// Set connecting tiles of bottom face edges
			this.facesArray[3][this.dimension+1][dim] = this.facesArray[0][1][dim];
			this.facesArray[3][0][dim] = this.facesArray[1][1][dim];
			this.facesArray[3][dim][0] = this.facesArray[5][dim][1];
			this.facesArray[3][dim][this.dimension+1] = this.facesArray[4][dim][1];

			// Set connecting tiles of right face edges
			this.facesArray[4][this.dimension+1][dim] = this.facesArray[0][dim][this.dimension];
			this.facesArray[4][dim][this.dimension+1] = this.facesArray[2][dim][this.dimension];
			this.facesArray[4][dim][0] = this.facesArray[3][dim][this.dimension];
			this.facesArray[4][0][dim] = this.facesArray[1][dim][this.dimension];

			// Set connecting tiles of left face edges
			this.facesArray[5][this.dimension+1][dim] = this.facesArray[0][dim][1];
			this.facesArray[5][dim][this.dimension+1] = this.facesArray[2][dim][1];
			this.facesArray[5][dim][0] = this.facesArray[3][dim][1];
			this.facesArray[5][0][dim] = this.facesArray[1][dim][1];
		}
	}

	public void randomizeCubeColors()
	{
		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				for(int face = 0; face < this.numFaces; face++)
				{
					for(int row = 1; row <= this.dimension; row++)
					{
						for(int column = 1; column <= this.dimension; column++)
						{
							this.facesArray[face][row][column].setMaterial(this.colorArray[(int)(Math.random()*2)]);
						}
					}
				}
			}
		}
	}

	public void removeFromWorld(Geometry world)
	{
		synchronized(this.LOCK)
		{
			if(this.isActive && world != null)
			{
				for(int face = 0; face < this.numFaces; face++)
				{
					for(int row = 1; row <= this.dimension; row++)
					{
						for(int column = 1; column <= this.dimension; column++)
						{
							this.center.delete(this.facesArray[face][row][column]);
							this.facesArray[face][row][column] = null;
						}
					}
				}

				world.delete(this.center);
				this.center = null;
				this.isActive = false;
			}
		}
	}

	public Geometry[][] getFace(int faceNum)
	{
		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				return this.facesArray[faceNum];
			}
		}
		return null;
	}

	public int getNumFaces()
	{
		return this.numFaces;
	}

	public int getDimension()
	{
		return this.dimension;
	}

	public Material getLitColor()
	{
		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				return this.litColor;
			}
		}
		return null;
	}

	public Material getUnlitColor()
	{
		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				return this.unlitColor;
			}
		}
		return null;
	}

	// Method to set a tile's material color
	public void setGeometryMaterial(Geometry cg, Material m)
	{
		if(cg != null)
			cg.setMaterial(m);
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
						Geometry tempGeo = this.facesArray[0][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, stepY, this.faceCenterOffset);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set back face
						tempGeo = this.facesArray[1][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, stepY, -this.faceCenterOffset);
						this.m.scale(this.tileScaleDimension, this.tileScaleDimension, this.tileThickness);

						// Set top face
						tempGeo = this.facesArray[2][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, this.faceCenterOffset, stepY);
						this.m.scale(this.tileScaleDimension, this.tileThickness, this.tileScaleDimension);

						// Set bottom face
						tempGeo = this.facesArray[3][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(stepX, -this.faceCenterOffset, stepY);
						this.m.scale(this.tileScaleDimension, this.tileThickness, this.tileScaleDimension);

						// Set right face
						tempGeo = this.facesArray[4][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(this.faceCenterOffset, stepX, stepY);
						this.m.scale(this.tileThickness, this.tileScaleDimension, this.tileScaleDimension);

						// Set left face
						tempGeo = this.facesArray[5][row][column];
						this.m = tempGeo.getMatrix();
						this.m.identity();
						this.m.translate(-this.faceCenterOffset, stepX, stepY);
						this.m.scale(this.tileThickness, this.tileScaleDimension, this.tileScaleDimension);
					}
				}
			}
		}
	}

	// BELOW TWO SCORE METHODS SHOULD BE PUSHED TO EACH GAME INSTANCE
	// AS DIFFERENT GAMES WILL CALCULATE SCORES BASED ON DIFFERENT LOGIC

	public  int getScore(){
		return getScore(litColor);
	}

	/**
	 * Iterates over all the cubes and returns the number of elements
	 * mhich mateches with the given material
	 * @param m
	 * @return
	 */
	public int getScore(Material m){

		int score = 0;
		for(int face = 0; face < this.numFaces; face++)
		{
			for(int row = 1; row <= this.dimension; row++)
			{
				for(int column = 1; column <= this.dimension; column++)
				{
					Material face_material = this.facesArray[face][row][column].getMaterial();
					if(face_material.equals(m)){
						score ++;
					}
				}
			}
		}

		return score;
	}
}