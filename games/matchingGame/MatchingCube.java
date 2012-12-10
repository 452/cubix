package games.matchingGame;

import elements.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import render.*;

public class MatchingCube extends GameCube
{
	// Variables to handle concurrency issues
	private boolean isActive = false;
	private final Object LOCK = new Object();
	public int clickTime = 0;

	// Matching game specific variables
	Material redColor, yellowColor, greenColor, blueColor, orangeColor, purpleColor;
	Material coveredColor, cleanColor;
	Material[] colorArray = new Material[6];

	Position previous_one, previous_two;
	// 3D array of tiles materials, faces order as follows: front, back, top, bottom, right, left
	Material[][][] matArray;

	//3D array indicating the state of the tiles as follows : 0 : covered ; 1 : clean ; 2 : shown
	int [][][] tileState;


	public MatchingCube(Geometry world, int dimension)
	{
		super(world, dimension);
		this.initialize(dimension);
	}
	
	public Color getOneColor(){
		if(this.previous_one == null){
			return null;
		}
		else{
			if(this.matArray[this.previous_one.face][this.previous_one.row][this.previous_one.column] == redColor){
				return Color.RED;
			}
			else if(this.matArray[this.previous_one.face][this.previous_one.row][this.previous_one.column] == yellowColor){
				return Color.YELLOW;
			}
			else if(this.matArray[this.previous_one.face][this.previous_one.row][this.previous_one.column] == greenColor){
				return Color.GREEN;
			}
			else if(this.matArray[this.previous_one.face][this.previous_one.row][this.previous_one.column] == blueColor){
				return Color.BLUE;
			}
			else if(this.matArray[this.previous_one.face][this.previous_one.row][this.previous_one.column] == orangeColor){
				return Color.ORANGE;
			}
			else{
				return Color.MAGENTA;
			}
		}
	}
	
	public Color getTwoColor(){
		if(this.previous_two == null){
			return null;
		}
		else{
			if(this.matArray[this.previous_two.face][this.previous_two.row][this.previous_two.column] == redColor){
				return Color.RED;
			}
			else if(this.matArray[this.previous_two.face][this.previous_two.row][this.previous_two.column] == yellowColor){
				return Color.YELLOW;
			}
			else if(this.matArray[this.previous_two.face][this.previous_two.row][this.previous_two.column] == greenColor){
				return Color.GREEN;
			}
			else if(this.matArray[this.previous_two.face][this.previous_two.row][this.previous_two.column] == blueColor){
				return Color.BLUE;
			}
			else if(this.matArray[this.previous_two.face][this.previous_two.row][this.previous_two.column] == orangeColor){
				return Color.ORANGE;
			}
			else{
				return Color.MAGENTA;
			}
		}
	}

	void initialize(int d)
	{
		// Initialize appropriate cube dimensions
		this.matArray = new Material[this.getNumFaces()][this.getDimension()+2][this.getDimension()+2];
		this.tileState = new int[this.getNumFaces()][this.getDimension()+2][this.getDimension()+2];

		// Set cube and tile materials
		this.cleanColor = new Material();
		this.cleanColor.setAmbient(0.7, 0.7, 0.7);
		this.cleanColor.setDiffuse(0.8, 0.8, 0.8);
		this.cleanColor.setSpecular(0.9, 0.9, 0.9, 10);

		this.coveredColor = new Material();
		this.coveredColor.setAmbient(0.1, 0.1, 0.1);
		this.coveredColor.setDiffuse(0.2, 0.2, 0.2);
		this.coveredColor.setSpecular(.5, .5, .5, 10);

		this.redColor = new Material();
		this.redColor.setAmbient(0.9, 0.1, 0.1);
		this.redColor.setDiffuse(0.95, 0.15, 0.15);
		this.redColor.setSpecular(.95, .15, .15, 10);

		this.yellowColor = new Material();
		this.yellowColor.setAmbient(0.9, 0.9, 0.1);
		this.yellowColor.setDiffuse(0.95, 0.95, 0.15);
		this.yellowColor.setSpecular(.95, .95, .15, 10);

		this.blueColor = new Material();
		this.blueColor.setAmbient(0.1, 0.1, 0.9);
		this.blueColor.setDiffuse(0.15, 0.15, 0.95);
		this.blueColor.setSpecular(.15, .15, .95, 10);

		this.greenColor = new Material();
		this.greenColor.setAmbient(0.1, 0.9, 0.1);
		this.greenColor.setDiffuse(0.15, 0.95, 0.15);
		this.greenColor.setSpecular(.15, .95, .15, 10);

		this.orangeColor = new Material();
		this.orangeColor.setAmbient(0.98, 0.5, 0.25);
		this.orangeColor.setDiffuse(0.99, 0.52, 0.27);
		this.orangeColor.setSpecular(.99, .52, .27, 10);

		this.purpleColor = new Material();
		this.purpleColor.setAmbient(0.9, 0.1, 0.9);
		this.purpleColor.setDiffuse(0.95, 0.15, 0.95);
		this.purpleColor.setSpecular(.95, .15, .95, 10);

		// Initialize all tiles materials for each face of the 3D cube
		for(int face = 0; face < this.getNumFaces(); face++)
		{
			for(int row = 1; row <= this.getDimension(); row++)
			{
				for(int column = 1; column <= this.getDimension(); column++)
				{
					this.getFace(face)[row][column].setMaterial(this.coveredColor);

					//all the tiles are covered initially
					this.tileState[face][row][column] = 0;
				}
			}
		}
	}

	public int getTileStateAt(int face, int row, int column){
		return this.tileState[face][row][column];
	}

	public boolean actionAt(int face, int row, int column){
		//if the tile has already been cleaned or shown, then no action
		if(this.tileState[face][row][column] != 0)
			return false;
		this.clickTime++;
		boolean isWin = false;

		if(this.previous_one != null && this.previous_two != null){
			if(this.isMatch()){
				this.doMatch();
			}
			else{
				this.doUnmatch();
			}

			this.previous_one = new Position(face, row, column);
			this.showTileAt(face, row, column);
			this.previous_two = null;
		}
		else if(this.previous_one == null){
			this.previous_one = new Position(face, row, column);
			this.showTileAt(face, row, column);
		}
		else if(this.previous_two == null){
			this.previous_two = new Position(face, row, column);
			this.showTileAt(face, row, column);
			isWin = isWin();
		}
		
		return isWin;
	}
	
	public boolean isWin(){
		int unclean_count = 0;
		for(int face = 0; face < this.getNumFaces(); face++)
		{
			for(int row = 1; row <= this.getDimension(); row++)
			{
				for(int column = 1; column <= this.getDimension(); column++)
				{
					if(this.tileState[face][row][column] != 1){unclean_count ++;}
				}
			}
		}
		return unclean_count <= 6;
	}

	public boolean isMatch(){
		return this.getFace(this.previous_one.face)[this.previous_one.row][this.previous_one.column].getMaterial().equals(
				this.getFace(this.previous_two.face)[this.previous_two.row][this.previous_two.column].getMaterial());
	}

	public void doMatch(){
		this.cleanTileAt(this.previous_one.face, this.previous_one.row, this.previous_one.column);
		this.cleanTileAt(this.previous_two.face, this.previous_two.row, this.previous_two.column);
	}

	public void doUnmatch(){
		this.coverTileAt(this.previous_one.face, this.previous_one.row, this.previous_one.column);
		this.coverTileAt(this.previous_two.face, this.previous_two.row, this.previous_two.column);
	}

	public void showTileAt(int face, int row, int column){
		this.getFace(face)[row][column].setMaterial(this.matArray[face][row][column]);
		this.tileState[face][row][column] = 2;
	}

	public void coverTileAt(int face, int row, int column){
		this.getFace(face)[row][column].setMaterial(this.coveredColor);
		this.tileState[face][row][column] = 0;
	}

	public void cleanTileAt(int face, int row, int column){
		this.getFace(face)[row][column].setMaterial(this.cleanColor);
		this.tileState[face][row][column] = 1;
	}
	
	public int getColorId(Material mat){
		if (mat == this.redColor)
			return 1;
		else if(mat == this.yellowColor)
			return 2;
		else if(mat == this.blueColor)
			return 3;
		else if(mat == this.greenColor)
			return 4;
		else if(mat == this.purpleColor)
			return 5;
		else
			return 6;
	}

	public Material getRandomColor(){
		switch((int)(Math.random() * 6)+1){
		case 1 :
			return this.redColor;
		case 2 :
			return this.yellowColor;
		case 3 :
			return this.blueColor;
		case 4 :
			return this.greenColor;
		case 5 :
			return this.purpleColor;
		default :
			return this.orangeColor;
		}
	}
	public void randomizeCubeColors()
	{
		for(int face = 0; face < this.getNumFaces(); face++)
		{
			for(int row = 1; row <= this.getDimension(); row++)
			{
				for(int column = 1; column <= this.getDimension(); column++)
				{
					this.matArray[face][row][column] = this.getRandomColor();
				}
			}
		}
	}
}