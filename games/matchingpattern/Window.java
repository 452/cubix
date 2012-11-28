package games.matchingpattern;

public class Window {

	int face,row,col;
	Window matchingpair;
	boolean isDiscovered = false;
	String imageId;
	boolean isOpen =false;
	MatchingPatternCube owner;
	
	public Window(int face,int row , int col){
		this.face = face;
		this.row = row;
		this.col = col;
		
	}


}
