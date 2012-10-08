import java.util.ArrayList;
import java.util.Stack;



public class Pathfinder {

	/*
	 * Disclaimer:
	 * The pushable/pullabe paths for now at least assume that if a box has two opposite
	 * walkable sides, it is pushable in both directions.
	 * In reality, it would need a path from the players position to the pushing position in order to
	 * do so.
	 * 
	 * I don't think this is a problem, becuase the path chosen will always be the shortest possible,
	 * so weird (and unpushable) paths will be excluded by default. I think.
	 */

	public enum WalkMode { WALK, PUSH, PULL }

	public static String findPullablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.PULL);
	}

	public static String findPushablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.PUSH);
	}

	public static String findWalkablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.WALK);
	}

	public static String findPushablePath2(Coords from, Coords to, Board board) {
		Board b = board.clone();
		Stack<Coords> queue = new Stack<Coords>();
		int[][] counterMap = new int[board.getWidth()][board.getHeight()];
		queue.push(from);
		int lastCounter = 1;
		counterMap[from.x][from.y] = lastCounter;
		
		while (!queue.isEmpty()) {
			Coords c = queue.pop();
		
			ArrayList<Coords> adjacent = AdjacentCells(c, board, WalkMode.PUSH);
			lastCounter += 1;
			for (Coords a : adjacent){
				counterMap[a.x][a.y] = lastCounter;
				queue.push(a);
			}
		}
		
		if (counterMap[to.x][to.y]<1) {
			// NO path to.
			return "";
		}
		
		queue = new Stack<Coords>();
		queue.add(from);
		
		while (!queue.isEmpty()) {
			
		}
		
		return "";
	}
	/**
	 * Finds a walkable path from A to B, and returns it as a series of direction chars (U,D,L,R)
	 * @param from
	 * @param to
	 * @param board
	 * @return
	 */
	public static String findPath (Coords from, Coords to, Board board, WalkMode mode){
		Stack<Coords> queue = new Stack<Coords> ();
		int[][] counterMap = new int[board.getWidth()][board.getHeight()];
		queue.push(from);
		counterMap[from.x][from.y] = 1;
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (mode != WalkMode.WALK){
				if (counterMap[to.x][to.y] > 0)
					break;
			}else{
				if (c.equals(to))
					break;
			}
			ArrayList<Coords> cells = AdjacentCells (c, board, mode);
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
			Tools.printCounter (counterMap, 0);
		}

		//p("to, x: " + to.x + " y: " + to.y);
		try{
			if (!(counterMap[to.x][to.y] > 0))
				return "";
			String bestPath = extractPath (from, to, counterMap);
			if (false){
				System.out.println (mode+"path from "+from+" to "+to+":"+ bestPath);
			}
			return bestPath;
		} catch (ArrayIndexOutOfBoundsException e){
			return "";
		}
	}

	private static String extractPath(Coords from, Coords to, int[][] counterMap) {
		if (to.x == from.x && to.y == from.y) {
			return "";
		}
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
			return "";
		else
			return extractPath(from, new Coords(nextX, nextY), counterMap)+dir;
	}

	/**
	 * Returnera en lista p� rutor vi kan st� p�.
	 * @param cell
	 * @return
	 */
	private static ArrayList<Coords> AdjacentCells(Coords cell, Board board, WalkMode mode) {
		switch (mode){

		case PULL: return pullableAdjacentCells (cell, board);
		case PUSH: return pushableAdjacentCells (cell, board);
		default: return walkableAdjacentCells (cell, board);
		}
	}

	private static ArrayList<Coords> pullableAdjacentCells(Coords cell,
			Board b) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		Coords left = new Coords (cell.x-1, cell.y);
		Coords left2 = new Coords (cell.x-2, cell.y);
		if (b.isTileWalkable(left) && b.isTileWalkable(left2)){
			addCell (left, cells, b);
		}

		Coords right = new Coords (cell.x+1, cell.y);
		Coords right2 = new Coords (cell.x+2, cell.y);
		if (b.isTileWalkable(right) && b.isTileWalkable(right2)){
			addCell (right, cells, b);
		}

		Coords up = new Coords (cell.x, cell.y-1);
		Coords up2 = new Coords (cell.x, cell.y-2);
		if (b.isTileWalkable(up) && b.isTileWalkable(up2)){
			addCell (up, cells, b);
		}

		Coords down = new Coords (cell.x, cell.y+1);
		Coords down2 = new Coords (cell.x, cell.y+2);
		if (b.isTileWalkable(down) && b.isTileWalkable(down2)){
			addCell (down, cells, b);
		}
		return cells;
	}

	private static ArrayList<Coords> pushableAdjacentCells(Coords cell,
			Board b) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		Coords up = new Coords (cell.x-1, cell.y);
		Coords down = new Coords (cell.x+1, cell.y);
		Coords left = new Coords (cell.x, cell.y-1);
		Coords right = new Coords (cell.x, cell.y+1);
		if (b.isTileWalkable(up) && b.isTileWalkable(down)){
			addCell (up, cells, b);
			addCell (down, cells, b);
		}
		if (b.isTileWalkable(left) && b.isTileWalkable(right)){
			addCell (left, cells, b);
			addCell (right, cells, b);
		}
		return cells;
	}

	public static void addCell (Coords c, ArrayList<Coords> cells, Board b){
		try{
			cells.add(c);
		} catch(ArrayIndexOutOfBoundsException e){

		}
	}

	/**
	 * Returnera en lista p� rutor vi kan st� p�.
	 * @param cell
	 * @return
	 */
	private static ArrayList<Coords> walkableAdjacentCells(Coords cell, Board board) {
		ArrayList<Coords> list = Tools.createAdjacentCells(cell, board);
		for (int i = 0; i < list.size(); i++) {
			if (!board.isTileWalkable(list.get(i))) {
				list.remove(i);
				i--;
			}
		}
		return list;
	}

}
