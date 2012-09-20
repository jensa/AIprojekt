import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Bond implements Agent{
	
	private static final boolean DEBUG = false;
	int[][] counterMap;
	int boardWidth;
	int boardHeight;
	Stack<Board> states = new Stack<Board>();
	HashSet<String> passedStates = new HashSet<String>();
	private boolean[][] bipartite;
	

	@Override
	public String solve(Board solveBoard) {
		Board board = solveBoard;
		System.out.println("solve");
		boardHeight = board.getHeight ();
		boardWidth = board.getWidth ();
		
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
		states.push(board);
		System.out.println ("LOL");
		printBoxes (board.getBoxHash());
		System.out.println ("LOL");
		String path = "";
		while (!states.isEmpty()) {
			Board state = states.pop();
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
									states.push(b);
						}
					}
				}
			}
		}
		//moveBox(board.getPlayer(), new Coords(7,9), new Coords(8,9));
		
//		board.printMap();
		//moveBox(new Coords(9,6));
		
		//for (int i = 0; i <1; i++) {
		System.out.println(findPath (board.getPlayer(), board.getBoxes()[0], board));
		//}
		return path;
	}
	
	private void calculateBipartiteDeadLocks() {
		bipartite = new boolean[boardHeight][boardWidth];
		
		bipartite[1][1] = true;
		bipartite[1][2] = true;
		bipartite[1][3] = true;
		bipartite[1][4] = true;
		bipartite[3][3] = true;
		bipartite[2][5] = true;
		bipartite[3][6] = true;
		bipartite[4][7] = true;
		bipartite[4][4] = true;
		bipartite[5][5] = true;
		bipartite[10][6] = true;
		bipartite[10][7] = true;
		bipartite[10][8] = true;
		bipartite[10][9] = true;
		bipartite[10][10] = true;
		bipartite[5][10] = true;
		
		if (DEBUG) {
			//printBipartiteArray();
		}
	}
	
	private void printBipartiteArray() {
		for (int i = 0; i < boardHeight; i++) {
			for (int j = 0; j < boardWidth; j++) {
				if (bipartite[j][i]) {
					System.out.print("[x]");
				} else {
					System.out.print("[ ]");
				}
			}
			System.out.println("");
		}

	}
	private void p (String m){
		if (DEBUG)
			System.out.println(m);
	}

	public boolean isDeadLock(Board b) {
		return false;//bipartiteDeadLock(b);
	}
	
	private boolean bipartiteDeadLock(Board b) {
		Coords[] boxlist = b.getBoxes();
		for (int i = 0; i < boxlist.length-1; i++) {
			try {
				p("pos: " + boxlist[i].x + " " + boxlist[i].y);
//				printBipartiteArray();
				
				p(""+bipartite[boxlist[i].y][boxlist[i].x]);
				
				if (bipartite[boxlist[i].x][boxlist[i].y]==true) {
					return true;
				}
			} catch( Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	private ArrayList<Board> moveBox(Board board,Coords inFrom) {
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
		Coords to = board.nextCoordInDirection(inTo, from);
		Coords pushingPlayerPosition = getPushingPlayerPosition (inFrom, to);
		if (!board.isTileWalkable(pushingPlayerPosition) || !board.isTileWalkable(to))
			return null;
		String pathToPos = findPath (player, pushingPlayerPosition, board);
		if (pathToPos == null)
			return null;
		Board newBoard = board.clone();
		newBoard.appendPath(pathToPos);
		newBoard.getPlayer().x = pushingPlayerPosition.x;
		newBoard.getPlayer().y = pushingPlayerPosition.y;
		System.out.println("moving "+inFrom+" in direction "+inTo);
		newBoard.printMap();
		newBoard.movePlayer(inTo);
		if (deadLock (newBoard))
			return null;
		System.out.println("moved "+inFrom+" in direction "+inTo);
		printBoxes (newBoard.getBoxHash());
		newBoard.printMap();
		return newBoard;
	}
	
	private void printBoxes (ArrayList<Coords> bx){
		int kuk = 1;
		for (Coords lol : bx){
			System.out.println ("Box "+kuk+": "+lol);
			kuk++;
		}
	}

	private Coords getPushingPlayerPosition(Coords inFrom, Coords inTo) {
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

	public String findPath (Coords from, Coords to, Board board){
		Stack<Coords> queue = new Stack<Coords> ();
		counterMap = new int[boardWidth][boardHeight];
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
		printCounter ();
		//System.out.println("to, x: " + to.x + " y: " + to.y);
		return extractPath (from, to);
	}
	
	private String extractPath(Coords from, Coords to) {
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
			return extractPath(from, new Coords(nextX, nextY))+dir;
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
	private ArrayList<Coords> createAdjacentCells(Coords cell, Board board) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (new Coords (cell.x-1, cell.y), cells, board);
		addCell (new Coords (cell.x+1, cell.y), cells, board);
		addCell (new Coords (cell.x, cell.y+1), cells, board);
		addCell (new Coords (cell.x, cell.y-1), cells, board);
		return cells;
	}
	
	private void addCell (Coords c, ArrayList<Coords> cells, Board board){
		if (board.isTileWalkable(c))
			cells.add(c);
	}
	
	private void printCounter (){
		if (DEBUG)
			for (int i=0;i<boardHeight;i++){
				for (int j=0;j<boardWidth;j++){
					if (counterMap[j][i] > 9 || counterMap[j][i] < 0){
						System.out.print("["+counterMap[j][i]+"]");
					}else
						System.out.print("[0"+counterMap[j][i]+"]");
					
				}
				System.out.print("\n");
			}
	}
	
	
	
	
	

}
