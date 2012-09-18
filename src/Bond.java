import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Bond implements Agent{
	
	private static final boolean DEBUG = false;
	int[][] counterMap;
	int boardWidth;
	int boardHeight;
	Queue<Board> states = new LinkedList<Board>();
	private boolean[][] bipartite;
	

	@Override
	public String solve(Board solveBoard) {
		Board board = solveBoard;
		System.out.println("solve");
		boardHeight = board.getHeight ();
		boardWidth = board.getWidth ();
		Coords[] boxes = board.getBoxes();
		
		calculateBipartiteDeadLocks();
		
		board.printMap();
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
		while (!states.isEmpty()) {
			Board state = states.poll();
			if (!isDeadLock(state)) {
				for (Coords c : boxes) {
					moveBox(state.getPlayer(), new Coords(8,8), state);
				}
			}
		}
		//moveBox(board.getPlayer(), new Coords(7,9), new Coords(8,9));
		
		board.printMap();
		//moveBox(new Coords(9,6));
		
		//for (int i = 0; i <1; i++) {
		System.out.println(findPath (board.getPlayer(), boxes[0], board));
		//}
		return "YOLO";
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
		bipartite[10][10] = true;
		bipartite[5][10] = true;
		
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

	public boolean isDeadLock(Board b) {
		return bipartiteDeadLock(b);
	}
	
	private boolean bipartiteDeadLock(Board b) {
		return true;
	}
	private void moveBox(Coords player, Coords from, Board board) {
		moveBox(player, from, board.nextCoordInDirection(Board.Direction.UP, from), board);
		moveBox(player, from, board.nextCoordInDirection(Board.Direction.DOWN, from), board);
		moveBox(player, from, board.nextCoordInDirection(Board.Direction.LEFT, from), board);
		moveBox(player, from, board.nextCoordInDirection(Board.Direction.RIGHT, from), board);
	}
	
	private void moveBox(Coords player, Coords from, Coords to, Board board) {
		ArrayList<Coords> adjacentCoords = walkableAdjacentCells(from, board);
		System.out.println("walkable size: " + adjacentCoords.size());
		for (int i = 0; i < adjacentCoords.size(); i++) {
			//System.out.println("adjacent coords: " + adjacentCoords.get(i).x + " " + adjacentCoords.get(i).y);
			String pathToACellNextToFrom = findPath(player, adjacentCoords.get(i), board);
			//System.out.println("Path: " + pathToACellNextToFrom);
			if (pathToACellNextToFrom != null) {
				//vi kan gå till rutan brevid
				Board newBoard = board.clone();
				newBoard.getPlayer().x = adjacentCoords.get(i).x;
				newBoard.getPlayer().y = adjacentCoords.get(i).y;
				newBoard.printMap();
				Board.Direction dir = getDirection(adjacentCoords.get(i), from);
				newBoard.movePlayer(dir);
				
				newBoard.printMap();
				states.add(newBoard);
				System.out.println("original");
				board.printMap();
			} else {
				// spelaren kan inte gå hit.
			}
		}
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
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (c.equals(to))
				break;
			ArrayList<Coords> cells = createAdjacentCells (c, board);
			for (int i=0; i<cells.size();i++){
				Coords adjacentCell = cells.get(i);
				boolean lowerCounterExists = counterMap [adjacentCell.x][adjacentCell.y] != 0 &&
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
		counterMap[from.x][from.y] = 0;
		counterMap[to.x][to.y] = -1;
		printCounter ();
		System.out.println("to, x: " + to.x + " y: " + to.y);
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
