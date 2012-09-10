import java.util.ArrayList;


public interface Board {
	
	public enum Direction{
		UP,DOWN,LEFT,RIGHT
	}
	
	public void movePlayer (Direction dir);
	
	public char getTileAt (int x, int y);
	
	public boolean isTileWalkable (int x, int y);
	
	public Coord[] getGoals ();
	
	public Coord getPlayer ();
	
	public Coord[] getBoxes ();
	

}
