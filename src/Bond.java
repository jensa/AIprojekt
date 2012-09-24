import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;


public class Bond implements Agent{

	private static final boolean DEBUG = false;
	int[][] counterMap;
	int boardWidth;
	int boardHeight;
	PriorityQueue<Board> states = new PriorityQueue<Board>();
	HashSet<String> passedStates = new HashSet<String>();
	private boolean[][] bipartite;
//	static boolean[][] deadlockMatrix;


	@Override
	public String solve(Board solveBoard) {
		Board board = solveBoard;
		System.out.println("solve");
		boardHeight = board.getHeight ();
		boardWidth = board.getWidth ();
		bipartite = MatrixTools.createMatrix(solveBoard);
//		deadlockMatrix = calculateDeadlock (board);
		MatrixTools.printBipartiteArray(bipartite, boardHeight, boardWidth);
		//		calculateBipartiteDeadLocks();

		//		board.printMap();
		/**
		board.movePlayer(Board.Direction.DOWN);

		board.printMap();

		board.movePlayer(Board.Direction.DOWN);

		System.out.println("player: " + board.getPlayer().x + " " + board.getPlayer().y);

		board.printMap();

		board.movePlayer(Board.Direction.DOWN);

		System.out.println("player: " + board.getPlayer().x + " " + board.getPlayer().y);

		board.printMap();
		board.movePlayer(Board.Direction.RIGHT);

		System.out.println("player: " + board.getPlayer().x + " " + board.getPlayer().y);

		board.printMap();
		 */

		System.out.println("original");
		board.printMap();
		states.add(board);
		String path = "";
		while (!states.isEmpty()) {
			Board state = states.poll();
			String hashCode = state.hash();
			if (!passedStates.contains(hashCode)) {
				if (state.isSolved()) {
					System.out.println("We did it");
					state.printMap();
					path = state.getPath();
					break;
				} else {
					passedStates.add(hashCode);
					if (!isDeadLock(state)) {
						//state.printMap();
						for (Coords c : state.getBoxes()) {
							for (Board b : moveBox(state,c))
								states.add(b);
						}
					}
				}
			}
		}
		//moveBox(board.getPlayer(), new Coords(7,9), new Coords(8,9));

		//		board.printMap();
		//moveBox(new Coords(9,6));

		//for (int i = 0; i <1; i++) {
//		System.out.println(findPath (board.getPlayer(), board.getBoxes()[0], board));
		//}
//		for (int i=0;i<20;i++){
//			p (();
//		}
//		doWalk (path, board);
		return path;
	}

	private void doWalk(String path, Board board) {
		char[] pathc = path.toCharArray();
		Board b = board;
		for (char step : pathc){
			Board.Direction dir = null;
			switch (step){
			case 'U': dir = Board.Direction.UP;break;
			case 'D': dir = Board.Direction.DOWN;break;
			case 'L': dir = Board.Direction.LEFT;break;
			case 'R': dir = Board.Direction.RIGHT;break;
			}
			board.movePlayer(dir);
			board.printMap();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean[][] calculateDeadlock(Board b) {
		char[][] backingMatrix = b.getBackingMatrix();
		boolean[][] bipartite = new boolean[b.getWidth ()][b.getHeight()];
		Coords[] boxes = b.getBoxes();

		for (Coords c : boxes){
			bipartite[c.x][c.y] = isDeadlockSquare (c, backingMatrix);
		}
		return bipartite;
	}

	private boolean isDeadlockSquare(Coords c, char[][] backingMatrix) {
		if (backingMatrix[c.x][c.y] == Surf.boxGoal)
			return false;
		if (boxClumps (c, backingMatrix))
			return true;
//		if (boxAgainstGoallessWall (c, backingMatrix))
//			return true;
		return false;
	}
	
	private boolean boxAgainstGoallessWall (Coords c, char[][] backingMatrix){
		boolean leftOrRightWall = wall (Board.Direction.LEFT, c, backingMatrix) || wall (Board.Direction.RIGHT, c, backingMatrix);
		if (leftOrRightWall && !goalInColumn (c, backingMatrix))
			return true;
		boolean upOrDownWall = wall (Board.Direction.UP, c, backingMatrix) || wall (Board.Direction.DOWN, c, backingMatrix);
		if (upOrDownWall && !goalInRow (c, backingMatrix))
			return true;
		return false;
	}
	
	private boolean goalInRow(Coords c, char[][] backingMatrix) {
		for (int i=0;i<backingMatrix.length-1;i++){
			if (backingMatrix[i][c.y] == Surf.boxGoal || backingMatrix[i][c.y] == Surf.goal)
				return true;
		}
		return false;
	}

	private boolean goalInColumn(Coords c, char[][] backingMatrix) {
		try{
		for (int i=0;i<backingMatrix[0].length-1;i++){
			if (backingMatrix[c.x][i] == Surf.boxGoal || backingMatrix[c.x][i] == Surf.goal)
				return true;
		}
		} catch (Exception e){
			int lol= 0;
			lol = lol+1;
		}
		return false;
	}

	private boolean wall(Board.Direction dir, Coords c, char[][] b) {
		char[] tiles = new char[3];
		switch (dir){
		case RIGHT:
			tiles[0]=b[c.x+1][c.y-1];tiles[1]= b[c.x+1][c.y]; tiles[2]= b[c.x+1][c.y+1];break;
		case LEFT:
			tiles[0]=b[c.x-1][c.y-1];tiles[1]= b[c.x-1][c.y]; tiles[2]= b[c.x-1][c.y+1];break;
		case UP:
			tiles[0]=b[c.x-1][c.y-1];tiles[1]= b[c.x][c.y-1]; tiles[2]= b[c.x+1][c.y-1];break;
		case DOWN:
			tiles[0]=b[c.x-1][c.y+1];tiles[1]= b[c.x][c.y+1]; tiles[2]= b[c.x+1][c.y+1];break;
		}
		return isTilesWalls (tiles);
	}

	private boolean isTilesWalls(char[] tiles) {
		for (char c : tiles)
			if (c != Surf.wall)
				return false;
		return true;
	}

	private boolean boxClumps (Coords c, char[][] backingMatrix){
		if (backingMatrix[c.x][c.y] == Surf.goal || backingMatrix[c.x][c.y] == Surf.boxGoal)
			return false;
		ArrayList<Coords> adjacent = createAdjacentCells(c, backingMatrix);
		if (adjacent.size() > 2)
			return true;
		if (adjacent.size() == 2){
			Coords one = adjacent.get(0);
			Coords two = adjacent.get(1);
			if (one.x != two.x && one.y != two.y)
				return true;
		}
		return false;
	}



	public ArrayList<Coords> createAdjacentCells(Coords cell, char[][] m) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (new Coords (cell.x-1, cell.y), cells, m);
		addCell (new Coords (cell.x+1, cell.y), cells, m);
		addCell (new Coords (cell.x, cell.y+1), cells, m);
		addCell (new Coords (cell.x, cell.y-1), cells, m);
		return cells;
	}

	private  void addCell (Coords c, ArrayList<Coords> cells, char[][] m){
		try{
			if (isBoxOrWall (m, c))
				cells.add(c);
		} catch(ArrayIndexOutOfBoundsException e){

		}
	}

	private boolean isBoxOrWall (char[][] m, Coords c){
		if (m[c.x][c.y] == Surf.wall || m[c.x][c.y] == Surf.boxGoal || m[c.x][c.y] == Surf.box)
			return true;
		return false;
	}
	private void p (String m){
		if (DEBUG)
			p (m);
	}

	public boolean isDeadLock(Board b) {
		return false;//bipartiteDeadLock(b);
	}
	
	private ArrayList<Board> moveBox(Board board,Coords inFrom) {
		ArrayList<Board> goalMoves = moveBoxToGoal (board, inFrom);
		goalMoves.addAll(moveBoxInAllDirections (board, inFrom));
		return goalMoves;
	}
	
	private ArrayList<Board> moveBoxToGoal(Board board, Coords inFrom) {
		Coords[] goals = board.getGoals();
		Coords from = new Coords (inFrom.x, inFrom.y);
		ArrayList<Board> boards = new ArrayList<Board> ();
		for (Coords c : goals){
			if (board.getTileAt(c) == Surf.goal){
				addState (moveBoxToCoords (from, c, board), boards);
			}
		}
		return boards;
	}
	
	private Board moveBoxToCoords (Coords from, Coords to, Board board){
		String pathToPos = findPath (from, to, board);
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

	private ArrayList<Board> moveBoxInAllDirections(Board board,Coords inFrom) {
		Coords from = new Coords (inFrom.x, inFrom.y);
		ArrayList<Board> boards = new ArrayList<Board> ();
		addState (moveBox(from, Board.Direction.UP, board), boards);
		addState (moveBox(from, Board.Direction.DOWN, board), boards);
		addState (moveBox(from, Board.Direction.LEFT, board), boards);
		addState (moveBox(from, Board.Direction.RIGHT, board), boards);
		return boards;
	}

	private void addState(Board board, ArrayList<Board> boards) {
		if (board != null)
			boards.add(board);
	}

	private Board moveBox(Coords inFrom, Board.Direction inTo, Board board) {
		Coords player = board.getPlayer();
		Coords from = new Coords (inFrom.x, inFrom.y);
		Coords to = CoordHelper.nextCoordInDirection(inTo, from);
		if (bipartite[to.y][to.x])
			return null;
		Coords pushingPlayerPosition = getPushingPlayerPosition (inFrom, to);
		if (!board.isTileWalkable(pushingPlayerPosition) || !board.isTileWalkable(to))
			return null;
		String pathToPos = findPath (player, pushingPlayerPosition, board);
		if (pathToPos == null)
			return null;
		Board newBoard = board.clone();
		String movedDirection = getMovedDirection (inTo);
		if (!newBoard.appendPath(pathToPos+movedDirection))
			return null;
		newBoard.getPlayer().x = pushingPlayerPosition.x;
		newBoard.getPlayer().y = pushingPlayerPosition.y;
		newBoard.movePlayer(inTo);
		if (board.getTileAt(from) == Surf.boxGoal && newBoard.getTileAt(to) == Surf.boxGoal)
			newBoard.modScore(-60);
		newBoard.printMap();
		boolean[][] deadlockMatrix = calculateDeadlock (newBoard);
		if (deadlockMatrix[to.x][to.y])
			return null;
		if (deadLock (newBoard))
			return null;
		newBoard.printMap();
		return newBoard;
	}

	public static String getMovedDirection(Board.Direction inTo) {
		switch (inTo){
		case RIGHT: return "R";
		case LEFT: return "L";
		case DOWN: return "D";
		case UP: return "U";
		}
		return null;
	}

	private void printBoxes (ArrayList<Coords> bx){
		int kuk = 1;
		for (Coords lol : bx){
			p ("Box "+kuk+": "+lol);
			kuk++;
		}
	}

	public static Coords getPushingPlayerPosition(Coords inFrom, Coords inTo) {
		int x = inFrom.x;
		int y = inFrom.y;
		int modX = inFrom.x - inTo.x;
		int modY = inFrom.y - inTo.y;
		return new Coords (x + modX, y+modY);
	}

	private boolean deadLock(Board board) {
		return false;
	}


	private Board.Direction getDirection(Coords from, Coords to) {
		// de måste vara en ifrån
		if (from.x + 1 == to.x && from.y == to.y) {
			return Board.Direction.RIGHT;
		}
		if (from.x - 1 == to.x && from.y == to.y) {
			return Board.Direction.LEFT;
		}
		if (from.x == to.x && from.y + 1 == to.y) {
			return Board.Direction.DOWN;
		}
		if (from.x == to.x && from.y - 1 == to.y) {
			return Board.Direction.UP;
		}

		return null; // null == ogiltigt drag.
	}

	public static String findPath (Coords from, Coords to, Board board){
		Stack<Coords> queue = new Stack<Coords> ();
		int[][] counterMap = new int[board.getWidth()][board.getHeight()];
		queue.push(from);
		counterMap[from.x][from.y] = 1;
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (c.equals(to))
				break;
			ArrayList<Coords> cells = createAdjacentCells (c, board);
			for (int i=0; i<cells.size();i++){
				Coords adjacentCell = cells.get(i);
				boolean lowerCounterExists = counterMap [adjacentCell.x][adjacentCell.y] > 0 &&
						counterMap [adjacentCell.x][adjacentCell.y] < counterMap [c.x][c.y]+1;
				if (lowerCounterExists){
					cells.remove(i);
					i--;
				}
				else{
					counterMap [adjacentCell.x][adjacentCell.y] = counterMap [c.x][c.y]+1;
				}
			}
			for (Coords newCoords : cells)
				queue.push(newCoords);
		}
		printCounter (counterMap);
		//p("to, x: " + to.x + " y: " + to.y);
		return extractPath (from, to, counterMap);
	}

	private static String extractPath(Coords from, Coords to, int[][] counterMap) {
		if (to.x == from.x && to.y == from.y) {
			return "";
		}
		int scoreOfCurrentCoord = counterMap[to.x][to.y];
		int nextX = to.x;
		int nextY = 0;
		String dir = "";
		int nextBestScore = 1000;
		if (counterMap[to.x+1][to.y] > 0 && counterMap[to.x+1][to.y] < nextBestScore|| (from.x == to.x+1 && from.y == to.y)) {
			nextX = to.x+1;
			nextY = to.y;
			nextBestScore = counterMap[nextX][nextY];
			dir = "L";
		}	
		if (counterMap[to.x-1][to.y] > 0 && counterMap[to.x-1][to.y] < nextBestScore|| (from.x == to.x-1 && from.y == to.y)) {
			nextX = to.x-1;
			nextY = to.y;
			nextBestScore = counterMap[nextX][nextY];
			dir = "R";
		}
		if (counterMap[to.x][to.y+1] > 0 && counterMap[to.x][to.y+1] < nextBestScore || (from.x == to.x && from.y == to.y +1)) {
			nextX = to.x;
			nextY = to.y+1;
			nextBestScore = counterMap[nextX][nextY];
			dir = "U";
		}
		if (counterMap[to.x][to.y-1] > 0 && counterMap[to.x][to.y-1] < nextBestScore || (from.x == to.x && from.y == to.y -1)) {
			nextX = to.x;
			nextY = to.y-1;
			nextBestScore = counterMap[nextX][nextY];
			dir = "D";
		}
		if (dir=="")
			return null;
		else
			return extractPath(from, new Coords(nextX, nextY), counterMap)+dir;
	}

	/**
	 * Returnera en lista på rutor vi kan stå på.
	 * @param cell
	 * @return
	 */
	private ArrayList<Coords> walkableAdjacentCells(Coords cell, Board board) {
		ArrayList<Coords> list = createAdjacentCells( cell, board);
		for (int i = 0; i < list.size(); i++) {
			if (!board.isTileWalkable(list.get(i))) {
				list.remove(i);
			}
		}
		return list;
	}
	private static ArrayList<Coords> createAdjacentCells(Coords cell, Board board) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (new Coords (cell.x-1, cell.y), cells, board);
		addCell (new Coords (cell.x+1, cell.y), cells, board);
		addCell (new Coords (cell.x, cell.y+1), cells, board);
		addCell (new Coords (cell.x, cell.y-1), cells, board);
		return cells;
	}

	private static void addCell (Coords c, ArrayList<Coords> cells, Board board){
		if (board.isTileWalkable(c))
			cells.add(c);
	}

	private static void printCounter (int[][] counterMap){
		if (DEBUG)
			for (int i=0;i<counterMap.length;i++){
				for (int j=0;j<counterMap[0].length;j++){
					if (counterMap[j][i] > 9 || counterMap[j][i] < 0){
						System.out.print("["+counterMap[j][i]+"]");
					}else
						System.out.print("[0"+counterMap[j][i]+"]");

				}
				System.out.print("\n");
			}
	}






}
