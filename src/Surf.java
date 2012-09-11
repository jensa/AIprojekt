import java.util.ArrayList;


public class Surf implements Board{
	
	private boolean solved;
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
	private ArrayList<Coords> boxes;
	
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
			}
		}
	}

	@Override
	public void movePlayer(Direction dir) {
		int newX = 0; int newY = 0;
		switch (dir){
		case UP: 
			newX = playerPosition.getX();
			newY = playerPosition.getY()-1;
			break;
		case DOWN: 
			newX = playerPosition.getX();
			newY = playerPosition.getY()+1;
			break;
		case LEFT: 
			newX = playerPosition.getX()-1;
			newY = playerPosition.getY();
			break;
		case RIGHT: 
			newX = playerPosition.getX()+1;
			newY = playerPosition.getY();
			break;
		}
		Coords newPos = new Coords (newX, newY);
		boolean emptyPosition = isTileWalkable (newPos);
		if (emptyPosition){
			doMove (dir);
		}
		boolean boxPosition = isTileBox (newPos.getX(), newPos.getY());
		if (boxPosition){
			doBoxMove (dir);
		}
		boolean boxGoalPosition = isTileBoxGoal (newPos.getX(), newPos.getY());
		if (boxGoalPosition){
			doGoal (dir, newPos);
		}
		
		
	}
	
	private void doGoal(Direction dir, Coords goalPos) {
		removeBoxFromList (goalPos);
		//Update matrix
		boardMatrix[playerPosition.getX()][playerPosition.getY()] = empty;
		boardMatrix[goalPos.getX()][goalPos.getY()] = playerGoal;
		playerPosition = goalPos;
		
	}

	private void removeBoxFromList(Coords goalPos) {
		for (Coords c : boxes){
			if (c.equals(goalPos)){
				boxes.remove(c);
				break;
			}
		}
	}

	private boolean isTileBoxGoal(int x, int i) {
		// TODO Auto-generated method stub
		return false;
	}

	private void doBoxMove(Direction dir) {
		// TODO Auto-generated method stub
		
	}

	private boolean isTileBox (int x, int y){
		if (boardMatrix[x][y] == box)
			return true;
		return false;
	}

	private void doMove(Direction dir) {
		
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
	
	public boolean isTileWalkable(Coords co) {
		if (boardMatrix[co.getX()][co.getY()] == empty || boardMatrix[co.getX()][co.getY()] == goal)
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
		return playerPosition;
	}

	@Override
	public Coords[] getBoxes() {
		Coords[] boxz = new Coords[boxes.size()];
		return goals.toArray(boxz);
	}
	
	public boolean isSolved (){
		return solved;
	}

}
