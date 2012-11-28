package games.simonGame;

import elements.*;
import render.*;
import java.awt.Color;

public class SimonCube extends GameCube
{
	private boolean faceHit = false;
	private int hitFace = 0, timeOutCount = 15, currentCount = 15;
	private Geometry[] flashGeoArray = new Geometry[3];
	private Color ambientColor, diffuseColor;

	public SimonCube(Geometry world)
	{
		super(world, 1);

		synchronized(this.LOCK)
		{
			for(int face = 0; face < this.numFaces; face++)
			{
				// Only one tile per face for Simon, but doesn't hurt iterating anyway
				for(int i = 1; i <= this.dimension; i++)
				{
					this.facesArray[face][i][i].setMaterial(SimonGame.materialArray[face]);
				}
			}

			// Set the flash geometry array
			for(int i = 0; i < this.flashGeoArray.length; i++)
			{
				Geometry tempGeo = this.center.add().cube();
				Material tempMaterial = new Material();
				tempMaterial.setAmbient(.5, .5, .5);
				tempMaterial.setDiffuse(.8, .8, .8);
				tempMaterial.setSpecular(.9, .9, .9, 10);
				tempMaterial.setTransparency(0.75);
				tempGeo.setMaterial(tempMaterial);
				this.flashGeoArray[i] = tempGeo;
			}
		}
	}

	@Override
	public void removeFromWorld(Geometry world)
	{
		synchronized(this.LOCK)
		{
			if(this.isActive && world != null)
			{
				this.isActive = false;

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

				// Delete the flash geometry array as well
				for(int i = 0; i < this.flashGeoArray.length; i++)
				{
					this.center.delete(this.flashGeoArray[i]);
					this.flashGeoArray[i] = null;
				}

				world.delete(this.center);
				this.center = null;
			}
		}
	}

	void setFaceHit(int f)
	{
		synchronized(this.LOCK)
		{
			this.faceHit = true;
			this.hitFace = f;
			this.currentCount = this.timeOutCount;
			this.ambientColor = SimonGame.COLOR_ARRAY[f];
			this.diffuseColor = SimonGame.DIFFUSE_COLOR_ARRAY[f];
			for(int i = 0; i < this.flashGeoArray.length; i++)
			{
				this.flashGeoArray[i].setFace(f);
				this.flashGeoArray[i].getMaterial().setAmbient(
					(double)ambientColor.getRed()/255,
					(double)ambientColor.getGreen()/255,
					(double)ambientColor.getBlue()/255
				);
				this.flashGeoArray[i].getMaterial().setDiffuse(
					(double)diffuseColor.getRed()/255,
					(double)diffuseColor.getGreen()/255,
					(double)diffuseColor.getBlue()/255
				);
			}
		}
	}

	@Override
	public void animate(double time)
	{
		super.animate(time);

		synchronized(this.LOCK)
		{
			if(this.isActive)
			{
				// Flash the face if hit
				if(this.faceHit)
				{
					// Flash the appropriate face
					switch(this.hitFace)
					{
						case 0:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(stepX, stepY, this.faceCenterOffset+0.01+0.01*i);
								this.m.scale(
									this.tileScaleDimension+0.1*i,
									this.tileScaleDimension+0.1*i,
									this.tileThickness);
							}
							break;
						case 1:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(stepX, stepY, -this.faceCenterOffset-0.01-0.01*i);
								this.m.scale(
									this.tileScaleDimension+0.1*i,
									this.tileScaleDimension+0.1*i,
									this.tileThickness);
							}
							break;
						case 2:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(stepX, this.faceCenterOffset+0.01+0.01*i, stepY);
								this.m.scale(
									this.tileScaleDimension+0.1*i,
									this.tileThickness,
									this.tileScaleDimension+0.1*i);
							}
							break;
						case 3:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(stepX, -this.faceCenterOffset-0.01-0.01*i, stepY);
								this.m.scale(
									this.tileScaleDimension+0.1*i,
									this.tileThickness,
									this.tileScaleDimension+0.1*i);
							}
							break;
						case 4:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(this.faceCenterOffset+0.01+0.01*i, stepX, stepY);
								this.m.scale(
									this.tileThickness,
									this.tileScaleDimension+0.1*i,
									this.tileScaleDimension+0.1*i);
							}
							break;
						case 5:
							for(int i = 0; i < this.flashGeoArray.length; i++)
							{
								this.m = this.flashGeoArray[i].getMatrix();
								this.m.identity();
								this.m.translate(-this.faceCenterOffset-0.01-0.01*i, stepX, stepY);
								this.m.scale(
									this.tileThickness,
									this.tileScaleDimension+0.1*i,
									this.tileScaleDimension+0.1*i);
							}
							break;
						default:
							break;
					}

					// Cheap counter to disable the flash effect
					if(--this.currentCount <= 0)
						this.faceHit = false;
				}
				else
				{
					for(int i = 0; i < this.flashGeoArray.length; i++)
					{
						this.m = this.flashGeoArray[i].getMatrix();
						this.m.identity();
					}
				}
			}
		}
	}
}