import java.util.ArrayList;
import java.util.Stack;



public class Pathfinder {

	public static final int INF = 999999;

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

	public static Path findPullablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.PULL);
	}

	public static Path findPushablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.PUSH);
	}

	public static Path findWalkablePath (Coords from, Coords to, Board board){
		return findPath (from, to, board, WalkMode.WALK);
	}
	
	public static char[][] getDistanceMatrixFromGoal(Board b) {
		char[][] returnMatrix = new char[b.getWidth ()][b.getHeight ()];
		Board bNoBox = b.noBoxClone();
		Coords[] goals = b.getGoals();
		
		for (Coords goal : goals) {
//			System.out.println("headmap goal");
			returnMatrix = getDistanceMatrixFromGoalForOneGoal(returnMatrix, bNoBox, goal);
			
		}
		/*
		for (int y = 0; y < returnMatrix[0].length; y++) {
			for (int x = 0; x < returnMatrix.length; x++) {
				if (returnMatrix[x][y] < 10) {
					System.out.print("[0" + (returnMatrix[x][y]+0) + "] ");
				} else {
					System.out.print("[" + (returnMatrix[x][y]+0) + "] ");
				}
			}
			System.out.println("");
			
		}*/
		return returnMatrix;
	}
	
	private static char[][] getDistanceMatrixFromGoalForOneGoal(char[][] goalMatrix, Board board, Coords goal) {
		Board b = board.noBoxClone();
		char[][] map = goalMatrix;
		Stack<Coords> queue = new Stack<Coords> ();
		boolean[][] visited = new boolean[b.getWidth()][b.getHeight()];
		map[goal.x][goal.y] = 1;
		queue.push(goal);
		
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			visited[c.x][c.y] = true;
			ArrayList<Coords> cells = Tools.createAdjacentCells(c, b);
			
			for (int i=0; i<cells.size();i++){
				Coords adjacentCell = cells.get(i);

				if (adjacentCell.x < 0 || adjacentCell.y < 0 || adjacentCell.y > b.getHeight() -1 || adjacentCell.x > b.getWidth() -1) {
					System.out.println("exit");
				} else if (b.getTileAt(adjacentCell) == Surf.goal) {
					map[adjacentCell.x][adjacentCell.y] = 1;
					if (!visited[adjacentCell.x][adjacentCell.y]) {
						queue.push(adjacentCell);
					}
					// found a goal. Should be 0.	
				} else if (b.getTileAt(adjacentCell) == Surf.wall) {
					int u = 0;
				} else if (map[adjacentCell.x][adjacentCell.y] > (map [c.x][c.y]+1) || (map[adjacentCell.x][adjacentCell.y] == 0) ) {
					char value = (char) (map [c.x][c.y]+1);
					
					map [adjacentCell.x][adjacentCell.y] = value;
					
					queue.push(adjacentCell);
				}
			}
			
		}
			

		return map;
	}
	

	/**
	 * Finds a walkable path from A to B, and returns it as a series of direction chars (U,D,L,R)
	 * @param from
	 * @param to
	 * @param board
	 * @return
	 */
	public static Path findPath (Coords from, Coords inTo, Board board, WalkMode mode){
		Coords to = new Coords (inTo.x, inTo.y);
		if (mode == WalkMode.PUSH)
			board.setIgnoreBox(from);
		if (from.equals(to))
			return new Path();
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
			ArrayList<Coords> cells = AdjacentCells (c, board, counterMap, mode, to);
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
					adjacentCell.par = c;
				}

			}
			for (Coords newCoords : cells)
				queue.push(newCoords);
			Tools.printCounter (counterMap, 0);
		}
		board.resetBoxes();
		try{
			if (mode != WalkMode.WALK &&!(counterMap[to.x][to.y] > 0))
				return null;
//			if (mode == WalkMode.PUSH)
//				System.out.println (from+" -> "+to+" mode "+mode);
			Path bestPath = extractPath (from, to, counterMap);
			Tools.printCounter(counterMap, 0);
			return bestPath;
		} catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
	}

	private static Path extractPath(Coords from, Coords to, int[][] counterMap) {
		if (to.x == from.x && to.y == from.y) {
			return null;
		}
		int nextX = to.x;
		int nextY = 0;
		char dir = 'q';
		int current = counterMap[to.x][to.y];
		// check right square
		int right = counterMap[to.x+1][to.y];
		if ((right > 0 && right+1 == current) || (from.x == to.x+1 && from.y == to.y)) {
				nextX = to.x+1;
				nextY = to.y;
				dir = 'L';
		}	
		//left
		int left = counterMap[to.x-1][to.y];
		if ((left > 0 && left+1 == current) || (from.x == to.x-1 && from.y == to.y)) {
				nextX = to.x-1;
				nextY = to.y;
				dir = 'R';
		}
		//down
		int down = counterMap[to.x][to.y+1];
		if ((down > 0 && down+1 == current ) || (from.x == to.x && from.y == to.y +1)) {
				nextX = to.x;
				nextY = to.y+1;
				dir = 'U';
		}
		//up
		int up = counterMap[to.x][to.y-1];
		if ((up > 0 && up+1 ==current) || (from.x == to.x && from.y == to.y -1)) {
				nextX = to.x;
				nextY = to.y-1;
				dir = 'D';
		}
		if (dir=='q')
			return null;
		else
			return new Path (extractPath(from, new Coords(nextX, nextY), counterMap),dir);
	}

	/**
	 * Returnera en lista p� rutor vi kan st� p�.
	 * @param cell
	 * @param to 
	 * @return
	 */
	private static ArrayList<Coords> AdjacentCells(Coords cell, Board board, int[][] map, WalkMode mode, Coords to) {
		switch (mode){

		case PULL: return pullableAdjacentCells (cell, board);
		case PUSH: return pushableAdjacentCells (cell, board, map);
		default: return walkableAdjacentCells (cell, board, to);
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
			Board b, int[][] map) {
		b.setNewBox(cell);
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		Coords left = new Coords (cell.x-1, cell.y);
		Coords right = new Coords (cell.x+1, cell.y);
		Coords up = new Coords (cell.x, cell.y-1);
		Coords down = new Coords (cell.x, cell.y+1);
		Coords assumedPlayerPos = cell.par; // man likely stands where the box came from
		boolean foundPlayer = assumedPlayerPos != null;
		if (b.isTileWalkable(up) && b.isTileWalkable(down)){
			if (foundPlayer){
				if (findPath (assumedPlayerPos, down, b, WalkMode.WALK) != null)
					addCell (up, cells, b);
				if (findPath (assumedPlayerPos, up, b, WalkMode.WALK) != null)
					addCell (down, cells, b);
			}else{
				addCell (up, cells, b);
				addCell (down, cells, b);
			}
		}
		if (b.isTileWalkable(left) && b.isTileWalkable(right)){
			if (foundPlayer){
				if (findPath (assumedPlayerPos, right, b, WalkMode.WALK) != null)
					addCell (left, cells, b);
				if (findPath (assumedPlayerPos, left, b, WalkMode.WALK) != null)
					addCell (right, cells, b);
			} else{
				addCell (left, cells, b);
				addCell (right, cells, b);
			}
		}
		b.resetNewBoxes();
		return cells;
	}

	private static void addCell (Coords c, ArrayList<Coords> cells, Board b){
		try{
			cells.add(c);
		} catch(ArrayIndexOutOfBoundsException e){

		}
	}

	public static Coords findClosestGoal (Coords from, Board b){
		int lengthToGoal = INF;
		Coords bestGoal = null;
		for (Coords goal : b.getGoals()){
			if (b.getTileAt(goal) == Surf.boxGoal)
				continue;
			Path p = findPushablePath (from, goal, b);
			if (p == null)
				continue;
			int length = p.toString().length();
			if (length < lengthToGoal){
				lengthToGoal = length;
				bestGoal = goal;
			}
		}
		if (bestGoal == null)
			return null;
		return new Coords (bestGoal.x, bestGoal.y);
	}
	/**
	 * Returnera en lista p� rutor vi kan st� p�.
	 * @param cell
	 * @param goal 
	 * @return
	 */
	private static ArrayList<Coords> walkableAdjacentCells(Coords cell, Board board, Coords goal) {
		ArrayList<Coords> list = Tools.createAdjacentCells(cell, board);
		for (int i = 0; i < list.size(); i++) {
			if (!list.get(i).equals(goal) && !board.isTileWalkable(list.get(i))) {
				list.remove(i);
				i--;
			}
		}
		return list;
	}

}
