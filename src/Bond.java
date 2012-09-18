import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Bond implements Agent{
	
	private static final boolean DEBUG = false;
	int[][] counterMap;
	int boardWidth;
	int boardHeight;
	Board board;

	@Override
	public String solve(Board board) {
		this.board = board;
		System.out.println("solve");
		boardHeight = board.getHeight ();
		boardWidth = board.getWidth ();
		Coords[] boxes = board.getBoxes();
		
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
		
		moveBox(board.getPlayer(), new Coords(2,4), new Coords(3,4));
		
		board.printMap();
		//moveBox(new Coords(9,6));
		
		//for (int i = 0; i <1; i++) {
		System.out.println(findPath (board.getPlayer(), boxes[0]));
		//}
		return "YOLO";
	}
	
	private void moveBox(Coords player, Coords from, Coords to) {
		
		ArrayList<Coords> adjacentCoords = walkableAdjacentCells(from);
		System.out.println("walkable size: " + adjacentCoords.size());
		for (int i = 0; i < adjacentCoords.size(); i++) {
			//System.out.println("adjacent coords: " + adjacentCoords.get(i).x + " " + adjacentCoords.get(i).y);
			String pathToACellNextToFrom = findPath(player, adjacentCoords.get(i));
			//System.out.println("Path: " + pathToACellNextToFrom);
			if (pathToACellNextToFrom != null) {
				//vi kan gå till rutan brevid
				board.getPlayer().x = adjacentCoords.get(i).x;
				board.getPlayer().y = adjacentCoords.get(i).y;
				board.printMap();
				board.movePlayer(getDirection(adjacentCoords.get(i), from));
				board.printMap();
				break;
			} else {
				// spelaren kan inte gå hit.
			}
		}
	}

	private Board.Direction getDirection(Coords from, Coords to) {
		// de måste vara en ifrån
		if (from.x + 1 == to.x && from.y == to.y) {
			return Board.Direction.RIGHT;
		}
		if (from.x - 1 == to.x && from.y == to.y) {
			return Board.Direction.LEFT;
		}
		if (from.x == to.x && from.y - 1 == to.y) {
			return Board.Direction.DOWN;
		}
		if (from.x == to.x && from.y + 1 == to.y) {
			return Board.Direction.UP;
		}
		
		return null; // null == ogiltigt drag.
	}

	public String findPath (Coords from, Coords to){
		Stack<Coords> queue = new Stack<Coords> ();
		counterMap = new int[boardWidth][boardHeight];
		queue.push(from);
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (c.equals(to))
				break;
			ArrayList<Coords> cells = createAdjacentCells (c);
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
	private ArrayList<Coords> walkableAdjacentCells(Coords cell) {
		ArrayList<Coords> list = createAdjacentCells( cell);
		for (int i = 0; i < list.size(); i++) {
			if (!board.isTileWalkable(list.get(i))) {
				list.remove(i);
			}
		}
		return list;
	}
	private ArrayList<Coords> createAdjacentCells(Coords cell) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (new Coords (cell.x-1, cell.y), cells);
		addCell (new Coords (cell.x+1, cell.y), cells);
		addCell (new Coords (cell.x, cell.y+1), cells);
		addCell (new Coords (cell.x, cell.y-1), cells);
		return cells;
	}
	
	private void addCell (Coords c, ArrayList<Coords> cells){
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
