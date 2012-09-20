import java.util.ArrayList;
import java.util.HashSet;


public interface Board {
	
	public enum Direction{
		UP,DOWN,LEFT,RIGHT
	}
	
	public Board clone();
		
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

	public ArrayList<Coords> getBoxHash();
	
	public ArrayList<Coords> getGoalsList();
	
	public Coords nextCoordInDirection(Direction dir, Coords from);
	
	public String hash();
	
	public String getPath ();
	
	public void appendPath (String path);
}
