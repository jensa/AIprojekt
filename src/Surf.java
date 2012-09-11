import java.util.ArrayList;
import java.util.HashSet;


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
	private HashSet<Coords> boxes;
	
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
		int xMod = 0; int yMod = 0;
		switch (dir){
		case UP: 
			yMod = -1;
			break;
		case DOWN: 
			yMod = 1;
			break;
		case LEFT: 
			xMod = -1;
			break;
		case RIGHT: 
			xMod = 1;
			break;
		}
		Coords newPos = new Coords (playerPosition.getX()+xMod, playerPosition.getY()+yMod);
		boolean emptyPosition = isTileWalkable (newPos);
		if (emptyPosition){
			doMove (newPos);
		}
		boolean boxPosition = isTileBox (newPos.getX(), newPos.getY());
		if (boxPosition){
			Coords nextTile = new Coords (newPos.getX()+xMod, newPos.getY()+yMod);
			boolean nextTileGoal = isTileGoal (nextTile.getX(), nextTile.getY());
			if (nextTileGoal && isTileWalkable (nextTile))
				doGoal (newPos);
			else if (isTileWalkable (nextTile))
				doBoxMove (newPos, nextTile);
		}
		
		
	}
	
	private void doMove(Coords newPos) {
		boardMatrix[playerPosition.getX()][playerPosition.getY()] = empty;
		boardMatrix[newPos.getX()][newPos.getY()] = player;
		playerPosition = newPos;
	}
	
	private void doBoxMove(Coords boxPos, Coords moveTo) {
		boardMatrix[playerPosition.getX()][playerPosition.getY()] = empty;
		boardMatrix[boxPos.getX()][boxPos.getY()] = player;
		boardMatrix[moveTo.getX()][moveTo.getY()] = box;
		
		playerPosition = boxPos;
		
		updateBox (boxPos, moveTo);
	}
	
	private void doGoal(Coords boxPos) {
		boxes.remove(boxPos);
		//Update matrix
		boardMatrix[playerPosition.getX()][playerPosition.getY()] = empty;
		boardMatrix[boxPos.getX()][boxPos.getY()] = player;
		playerPosition = boxPos;
		
	}

	private void updateBox(Coords from, Coords to) {
		boxes.remove(from);
		boxes.add(to);
	}
	
	private boolean isTileGoal(int x, int y) {
		if (boardMatrix[x][y] == goal)
			return true;
		return false;
	}
	
	private boolean isTileBox (int x, int y){
		if (boardMatrix[x][y] == box)
			return true;
		return false;
	}

	@Override
	public char getTileAt(int x, int y) {
		return boardMatrix[x][y];
	}
	
	private boolean isTileWalkable(Coords co) {
		return isTileWalkable (co.getX(), co.getY());
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
	
	public void printMap (){
		System.out.println("Map");
		for (int i=0;i<boardMatrix[0].length;i++){
			for (int j=0;j<boardMatrix[i].length;j++)
				System.out.print(boardMatrix[i][j]);
			System.out.print("\n");
		}
		System.out.println("-----------------------");
	}

}
