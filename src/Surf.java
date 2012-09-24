import java.util.ArrayList;
import java.util.HashSet;


public class Surf implements Board{

	private boolean solved;
	private String path;
	final int height;
	final int width;
	
	private int scoreMod;

	int nr = 0;

	public static final char wall = 0x23;
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
		path = "";
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
		path = b.getPath();
		boxes = new ArrayList<Coords> ();
		for (Coords c : b.getBoxHash())
			boxes.add(new Coords (c.x, c.y));
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
		for (int i=0;i<boxes.size();i++){
			if (boxes.get(i).equals(from)){
				boxes.get(i).x = to.x;
				boxes.get(i).y = to.y;
				break;
			}
		}
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
		for (int i=0;i<height;i++){
			for (int j=0;j<width;j++)
				if (new Coords(j,i).equals(this.playerPosition))
					System.out.print("[@]");
				else
					System.out.print("["+boardMatrix[j][i]+"]");
			System.out.print("\n");
		}
		System.out.println (hash ());
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
	public String hash() {
		double hash = 0;
		Coords[] b = new Coords[boxes.size()]; 
		for (int i=0; i<boxes.size();i++)
			b[i] = boxes.get(i);
		/*
		for (int i = 0; i < b.length - 1; i++) {
			double b1 =b[i].x*100;
			double a = b[i].y*1;
			double c = (a + b1)*(Math.pow(1000,b[i].id));
			hash += c;
		}*/
		StringBuilder builder = new StringBuilder(b.length*4);
		for (Coords c : b){
			builder.append(""+c.x+c.y);
		}
		return builder.toString();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean appendPath(String pathPart) {
		if (path.length() < 500)
			path = path.concat(pathPart);
		else
			return false;
		return true;


	}
	
	public int getScore (){
		int kuk = 0;
		for (Coords c : goals){
			if (getTileAt (c) == boxGoal)
				kuk += 10;
		}
		for (Coords c : boxes){
			if (moveBoxToGoal (this, c))
				kuk++;
		}
			
		if (kuk > 5){
			int lol = 0;
		lol = lol+1;
		}
		return kuk+scoreMod;
		
	}
	
	public void modScore (int mod){
		scoreMod += mod;
	}

	@Override
	public int compareTo(Board o) {
		if (o.getScore() > getScore ())
			return -1;
		else if (o.getScore() < getScore ())
			return 1;
		return 0;
	}
	
	private boolean moveBoxToGoal(Board board, Coords inFrom) {
		Coords[] goals = board.getGoals();
		Coords from = new Coords (inFrom.x, inFrom.y);
		ArrayList<Board> boards = new ArrayList<Board> ();
		for (Coords c : goals){
			if (board.getTileAt(c) == Surf.goal){
				Board boxMoved = moveBoxToCoords (from, c, board);
				if (boxMoved != null)
					return true;
				
			}
		}
		return false;
	}
	
	private Board moveBoxToCoords (Coords from, Coords to, Board board){
		String pathToPos = Bond.findPath (from, to, board);
		if (pathToPos == null)
			return null;
		char[] path = pathToPos.toCharArray();
		Board b = board;
		for (char step : path){
			Board.Direction dir = null;
			switch (step){
			case 'U': dir = Board.Direction.UP;break;
			case 'D': dir = Board.Direction.DOWN;break;
			case 'L': dir = Board.Direction.LEFT;break;
			case 'R': dir = Board.Direction.RIGHT;break;
			}
			b = moveBox (from, dir, b);
			if (b == null)
				return null;
			from = CoordHelper.nextCoordInDirection(dir, from);
			
		}
		b.modScore(20);
		return b;
	}
	
	private Board moveBox(Coords inFrom, Board.Direction inTo, Board board) {
		Coords player = board.getPlayer();
		Coords from = new Coords (inFrom.x, inFrom.y);
		Coords to = CoordHelper.nextCoordInDirection(inTo, from);
		Coords pushingPlayerPosition = Bond.getPushingPlayerPosition (inFrom, to);
		if (!board.isTileWalkable(pushingPlayerPosition) || !board.isTileWalkable(to))
			return null;
		String pathToPos = Bond.findPath (player, pushingPlayerPosition, board);
		if (pathToPos == null)
			return null;
		Board newBoard = board.clone();
		String movedDirection = Bond.getMovedDirection (inTo);
		if (!newBoard.appendPath(pathToPos+movedDirection))
			return null;
		newBoard.getPlayer().x = pushingPlayerPosition.x;
		newBoard.getPlayer().y = pushingPlayerPosition.y;
		newBoard.movePlayer(inTo);
		if (board.getTileAt(from) == Surf.boxGoal && newBoard.getTileAt(to) == Surf.boxGoal)
			newBoard.modScore(-60);
		newBoard.printMap();
		/*
		boolean[][] deadlockMatrix = calculateDeadlock (newBoard);
		if (deadlockMatrix[to.x][to.y])
			return null;
		if (deadLock (newBoard))
			return null;*/
		newBoard.printMap();
		return newBoard;
	}
}
