import java.util.ArrayList;


public interface Board {
	
	public enum Direction{
		UP,DOWN,LEFT,RIGHT
	}
	
	public void movePlayer (Direction dir);
	
	public char getTileAt (Coords c);
	
	public boolean isTileWalkable (Coords c);
	
	public Coords[] getGoals ();
	
	public Coords getPlayer ();
	
	public Coords[] getBoxes ();
	
	public void printMap ();

	public char[][] getBackingMatrix();
	
	public boolean isSolved ();

	public int getHeight();

	public int getWidth();
	

}
