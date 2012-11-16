/*
 * Computer Games - Fall 2012
 * Cubix - A 3D Game
 * Rotate the cube
 * Click on a tile to spread your color to its adjacent tiles
 * This will also reverse the color of the tile you clicked
 * The color with the most tiles at the end of the game wins
 *
 * USES CUSTOM MODIFIED RENDER.GEOMETRY CLASS
 */

import render.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class cubix extends RenderApplet implements ActionListener
{
	double point[] = new double[3];
	Matrix m;
	boolean isCapturedClick = false;
	Material litColor, unlitColor;
	Material[] colorArray = new Material[2];
	int dimension = 5, 	midDimension = this.dimension/2+1;
	Geometry center;
	private int numFaces = 6;
	// 3D array of tiles, faces order as follows: front, back, top, bottom, right, left
	Geometry[][][] facesArray = new Geometry[this.numFaces][this.dimension+2][this.dimension+2];

	// Gameplay variables
	boolean introScreenOn = true, gameOn = false, gameOver = false, gameWin = false;
	int level = 1;

	// Timer for delayed and timed actions
	private Timer timer;
	private int delay = 2000;

	public void initialize()
	{
		setBgColor(.7, .7, .9);
		addLight( 1, 1, 1, .8, .85, 1);
		addLight(-1,-1,-1, 1, 1, 1);

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

		this.center = getWorld().add();

		// Call method to initialize the game cube // MOVED TO INITLEVEL METHOD
		//this.initializeGameCube();
	}

	void initializeGameCube()
	{
		// Initialize all tiles for each face of the 3D cube
		for(int face = 0; face < this.numFaces; face++)
		{
			for(int row = 1; row <= this.dimension; row++)
			{
				for(int column = 1; column <= this.dimension; column++)
				{
					Geometry tempGeo = this.center.add().cube();
					tempGeo.setMaterial(this.colorArray[(int)(Math.random()*2)]);
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

	// Override the super class' stop method - stop any additional actions
	public void stop()
	{
		super.stop();

		// Make sure the timer stops at the stop of the applet
		if(this.timer != null)
		{
			this.timer.stop();
			this.timer = null;
		}
	}

	// Method to initialize a game level
	void initLevel()
	{
		// Call method to initialize the game cube
		this.initializeGameCube();

		// Call method to start timer and related timed actions (for computer actions)
		//this.startTimer();

		this.gameOn = true;
	}

	void startTimer()
	{
		this.timer = new Timer(this.delay, this);
		this.timer.setInitialDelay(this.delay);
		this.timer.start();
	}

	void stopTimer()
	{
		if(this.timer != null)
		{
			this.timer.stop();
			this.timer = null;
		}
	}

	public void drawOverlay(Graphics g)
	{
		if(this.introScreenOn)
		{
			int centerWidth = this.getWidth()/2;
			int centerHeight = this.getHeight()/2;
			g.setColor(Color.WHITE);
			g.fillRect(centerWidth-40, centerHeight-20, 80, 40);
			g.setColor(Color.BLACK);
			g.drawString("START", centerWidth-20, centerHeight);
		}
	}

	public boolean mouseDown(Event e, int x, int y)
	{
		// If not game on yet, do nothing on mouse click down
		if(!this.gameOn)
			return true;

		Geometry g = queryCursor(point);
		if(g != null)
		{
			this.isCapturedClick = true;
			return true;
		}

		return false;
	}

	public boolean mouseDrag(Event e, int x, int y)
	{
		if(this.isCapturedClick)
			return true;
		return false;
	}

	public boolean mouseUp(Event e, int x, int y)
	{
		if(this.introScreenOn)
		{
			int startX = this.getWidth()/2-40, endX = startX+80;
			int startY = this.getHeight()/2-20, endY = startY+40;

			if(x >= startX && x <= endX && y >= startY && y <= endY)
			{
				this.introScreenOn = false;
				this.initLevel();
			}

			return true;
		}
		else
		{
			if(this.isCapturedClick)
			{
				Geometry g = queryCursor(point);

				if(g != null)
				{
					// Get the tile and set its action when clicked
					this.clickTile(this.facesArray[g.getFace()], g.getPositionRow(), g.getPositionColumn());
				}

				isCapturedClick = false;
				return true;
			}
		}
		return false;
	}

	// Method to perform action on a tile when it is clicked
	void clickTile(Geometry[][] customGeoArray, int row, int column)
	{
		Material currentColor = customGeoArray[row][column].material;
		// Invert its color
		customGeoArray[row][column].setMaterial(currentColor == litColor ? unlitColor : litColor);
		// Spread the tile color to the touching side tiles
		this.setGeometryMaterial(customGeoArray[row-1][column], currentColor);
		this.setGeometryMaterial(customGeoArray[row+1][column], currentColor);
		this.setGeometryMaterial(customGeoArray[row][column-1], currentColor);
		this.setGeometryMaterial(customGeoArray[row][column+1], currentColor);
	}

	// Method to set a tile's material color
	void setGeometryMaterial(Geometry cg, Material m)
	{
		if(cg != null)
			cg.setMaterial(m);
	}

	double tileScaleDimension = 0.25, tileThickness = 0.01, faceCenterOffset = 1.25;

	public void animate(double time)
	{
		if(this.gameOn)
		{
		this.m = this.center.getMatrix();
		this.m.identity();
		//this.m.translate(0, 0, -2);

		// For each tile in the 2d array
		for(int row = 1; row <= this.dimension; row++)
		{
			for(int column = 1; column <= this.dimension; column++)
			{
				// Get its appropriate x and y coordinates, move and resize each tile
				double stepX, stepY;
				stepX = -((double)(this.midDimension-column))/2;
				stepY = -((double)(this.midDimension-row))/2;

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

	// Action to perform - ActionListener implementation
	public void actionPerformed(ActionEvent e)
	{
		if(this.gameOn && !this.gameOver)
		{
			// When timer calls action, get a random tile and call its clicked action
			int face = (int)(Math.random()*this.numFaces);
			int row = 1+(int)(Math.random()*this.dimension);
			int column = 1+(int)(Math.random()*this.dimension);
			this.clickTile(this.facesArray[face], row, column);
		}
	}
}
