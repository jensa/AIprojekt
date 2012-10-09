import java.util.ArrayList;


public interface Board extends Comparable<Board>{
	
	public enum Direction{
		UP,DOWN,LEFT,RIGHT
	}
	
	public Board clone();
	public Board noBoxClone ();
		
	public void movePlayer (Direction dir);
	
	public char getTileAt (Coords c);
	
	public boolean isTileWalkable (Coords c);
	
	public boolean isTileWalkable(Coords c, boolean ignoreBoxes);
	
	public boolean isTileAnyBox (Coords c);
	public boolean isTileWall (Coords c);
	
	public Coords[] getGoals ();
	
	public Coords getPlayer ();
	
	public Coords[] getBoxes ();
	
	public void printMap ();
	
	public void printMap (int sleep);

	public char[][] getBackingMatrix();
	
	public boolean isSolved ();

	public int getHeight();

	public int getWidth();

	public ArrayList<Coords> getBoxHash();
	
	public ArrayList<Coords> getGoalsList();
	
	public void removeGoal (Coords goal);
	
	public void removeBox (Coords box);
	
	public String hash();
	
	public Path getPath ();
	
	public boolean setPath (Path path);
	
	public int getScore ();
	
	public void modScore (int mod);
	
	public void setIgnoreBox(Coords box);
	
	public void resetBoxes ();
	
	public void setNewBox (Coords box);
	
	public void resetNewBoxes ();
	
	public void addTemporaryWall (Coords w);
	
	public void resetTempWalls ();
	
}
