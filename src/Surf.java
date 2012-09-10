import java.util.ArrayList;


public class Surf implements Board{
	
	private boolean startSolved;
	final int height;
	final int length;
	
	final int wall = 0x23;
	final int player = 0x40;
	final int playerGoal = 0x2b;
	final int box = 0x24;
	final int boxGoal = 0x2a;
	final int goal = 0x2e;
	final int empty = 0x20;
	
	private char[][] boardMatrix;
	private ArrayList<Coords> goals;
	private Coords playerPosition;
	
	public Surf (int longestRow, String[] rows){
		goals = new ArrayList<Coords> ();
		height = rows.length;
		length = longestRow;
		boardMatrix = new char[length][height];
		for (int i=0;i<rows.length;i++){
			addRow (rows[i], i);
		}
	}

	private void addRow (String row, int rowNum){
		for (int i=0;i<row.length();i++){
			char currentTile = row.charAt(i);
			boardMatrix[i][rowNum] = currentTile;
			if (currentTile == player)
				playerPosition = new Coords (i, rowNum);
			if (currentTile == goal){
				goals.add(new Coords (i, rowNum));
			} if (currentTile == playerGoal){
				startSolved = true;
				return;
			}
		}
	}

	@Override
	public void movePlayer(Direction dir) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public char getTileAt(int x, int y) {
		return boardMatrix[x][y];
	}

	@Override
	public boolean isTileWalkable(int x, int y) {
		if (boardMatrix[x][y] == empty || boardMatrix[x][y] == goal)
			return true;
		return false;
	}

	@Override
	public Coords[] getGoals() {
		Coords[] gols = new Coords[goals.size()];
		return goals.toArray(gols);
	}

	@Override
	public Coords getPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coords[] getBoxes() {
		// TODO Auto-generated method stub
		return null;
	}

}
