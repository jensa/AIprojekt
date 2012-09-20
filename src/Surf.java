import java.util.ArrayList;
import java.util.HashSet;


public class Surf implements Board{
	
	private boolean solved;
	final int height;
	final int width;
	
	int nr = 0;
	
	final int wall = 0x23;
	public static final char player = 0x40;
	public static final char playerGoal = 0x2b;
	public static final char box = 0x24;
	public static final char boxGoal = 0x2a;
	public static final char goal = 0x2e;
	public static final char empty = 0x20;
	
	public char[][] boardMatrix;
	public ArrayList<Coords> goals;
	public Coords playerPosition;
	public ArrayList<Coords> boxes;
	
	public Surf (int longestRow, String[] rows){
		boxes = new ArrayList<Coords> ();
		goals = new ArrayList<Coords> ();
		height = rows.length;
		width = longestRow;
		boardMatrix = new char[width][height];
		
		for (int i=0;i<rows.length;i++){
			addRow (rows[i], i);
		}
	}
	
	public Surf(Board b) {
		goals = (ArrayList<Coords>) b.getGoalsList().clone();
		boxes = (ArrayList<Coords>) b.getBoxHash().clone();
		width = b.getWidth();
		height = b.getHeight();
		boardMatrix = new char[width][height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.boardMatrix[i][j] = b.getBackingMatrix()[i][j];
			}
		}
		this.playerPosition = new Coords(b.getPlayer().x, b.getPlayer().y);
	}

	@Override
	public Board clone() {
		try {
			Board newBoard = new Surf(this);
			return newBoard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void addRow (String row, int rowNum){

		for (int i=0;i<row.length();i++){
			char currentTile = row.charAt(i);
			boardMatrix[i][rowNum] = currentTile;
			if (currentTile == playerGoal){
				playerPosition = new Coords (i, rowNum);
				boardMatrix[i][rowNum] = goal;
				currentTile = goal;
			}
			if (currentTile == player){
				playerPosition = new Coords (i, rowNum);
				boardMatrix[i][rowNum] = empty;
				currentTile = empty;
			}
			if (currentTile == goal || currentTile == boxGoal){
				goals.add(new Coords (i, rowNum));
			}
			if (currentTile == box) {
				boxes.add(new Coords (i, rowNum, nr));
				nr += 1;
			}
			if (currentTile == boxGoal) {
				boxes.add(new Coords(i, rowNum, nr));
				nr += 1;
			}
		}
	}
	
	public Coords nextCoordInDirection(Direction dir, Coords from) {
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
		return new Coords(from.x + xMod, from.y + yMod);
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
		if (isTileBox (newPos)){
			Coords nextTile = new Coords (newPos.getX()+xMod, newPos.getY()+yMod);
			if (isTileWalkable (nextTile) )
				doBoxMove (newPos, nextTile);
		}
	}
	
	private void doMove(Coords newPos) {
		playerPosition = newPos;
	}
	
	private void doBoxMove(Coords boxPos, Coords moveTo) {
		if (isTileGoal (boxPos))
			boardMatrix[boxPos.getX()][boxPos.getY()] = goal;
		else
			boardMatrix[boxPos.getX()][boxPos.getY()] = empty;
		if (isTileGoal (moveTo))
			boardMatrix[moveTo.getX()][moveTo.getY()] = boxGoal;
		else
			boardMatrix[moveTo.getX()][moveTo.getY()] = box;
		playerPosition = boxPos;
		updateBox (boxPos, moveTo);
	}

	private void updateBox(Coords from, Coords to) {
		boxes.remove(from);
		boxes.add(to);
	}
	
	private boolean isTileGoal(Coords c) {
		if (goals.contains(c))
			return true;
		return false;
	}
	
	private boolean isTileBox (Coords c){
		if (boardMatrix[c.getX()][c.getY()] == box | boardMatrix[c.getX ()][c.getY()] == boxGoal)
			return true;
		return false;
	}
	
	private boolean isTilePlayerGoal (Coords c){
		if (boardMatrix[c.getX()][c.getY()] == playerGoal)
			return true;
		return false;
	}

	@Override
	public char getTileAt(Coords c) {
		return boardMatrix[c.x][c.y];
	}

	@Override
	public boolean isTileWalkable(Coords c) {
		if (boardMatrix[c.x][c.y] == empty || boardMatrix[c.x][c.y] == goal)
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
		//Coords[] boxz = new Coords[boxes.size()];
		//return goals.toArray(boxz);
		Coords[] boxz = new Coords[boxes.size()];
		boxes.toArray(boxz);
		return boxz;
	}
	
	public boolean isSolved (){
		for (Coords c : boxes){
			if ( !goals.contains(c) )
				return false;
		}
		return true;
	}
	
	public void printMap (){
		System.out.println("Map");
		for (int i=0;i<height;i++){
			for (int j=0;j<width;j++)
				if (new Coords(j,i).equals(this.playerPosition))
					System.out.print("@");
				else
					System.out.print(boardMatrix[j][i]);
			System.out.print("\n");
		}
		System.out.println("-----------------------");
	}
	
	public char[][] getBackingMatrix (){
		return boardMatrix;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		// TODO Auto-generated method stub
		return width;
	}

	public ArrayList<Coords> getBoxHash() {
		return boxes;
	}

	@Override
	public ArrayList<Coords> getGoalsList() {
		return this.goals;
	}

	@Override
	public double hash() {
		double hash = 0;
		Coords[] b = new Coords[boxes.size()]; 
		boxes.toArray(b);
		for (int i = 0; i < b.length - 1; i++) {
			double b1 =b[i].x*100;
			double a = b[i].y*1;
			double c = (a + b1)*(Math.pow(1000,b[i].id));
			hash += c;
		}
		return hash;
	}
}
