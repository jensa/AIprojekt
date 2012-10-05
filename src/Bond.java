import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Stack;

/*
 * Likely TODO:
 * write pathfinding algorithm for pushing and pulling boxes to & from coords
 * findpath only finds walkable path, not pushable
 * the existing one can be reused, just use a different createWalkableCells()
 * which only creates neighbouring cells in which the opposite end is free
 * (and therefore pushable)
 * 
 * Create tunnelPush() check to weed out redundant states.
 * 
 * refactor class, this seems to be something needed to be done all the time.
 * 
 */
public class Bond implements Agent{

	int[][] counterMap;
	int boardWidth;
	int boardHeight;
	PriorityQueue<Board> states = new PriorityQueue<Board>();
	HashSet<String> passedStates = new HashSet<String>();
	private boolean[][] guaranteedDeadlocks;
	//	static boolean[][] deadlockMatrix;


	@Override
	public String solve(Board solveBoard) {
		Board board = solveBoard;
		System.out.println("solve");
		boardHeight = board.getHeight ();
		boardWidth = board.getWidth ();
		guaranteedDeadlocks = Tools.createMatrix(solveBoard);
		calculateAdditionalDeadlocks (board.noBoxClone());
		//		deadlockMatrix = calculateDeadlock (board);
		Tools.printBipartiteArray(guaranteedDeadlocks, boardHeight, boardWidth);
		if (boardHeight > 1){
			return "";
		}
		//		board.printMap();
		System.out.println("original");
		board.printMap();
		states.add(board);
		String path = "";
		while (!states.isEmpty()) {
			Board state = states.poll();
			if (passedStates.contains(state.hash()))
				continue;
			if (state.isSolved()) {
				System.out.println("We did it");
				state.printMap();
				path = state.getPath();
				break;
			} else {
				passedStates.add(state.hash ());
				if (!isDeadLock(state)) {
					for (Coords c : state.getBoxes()) {
						for (Board b : moveBox(state,c))
							states.add(b);
					}
				}

			}
		}
		return path;
	}
	/**
	 * TODO:
	 * Intended to claculate bipartite deadlocks before starting,
	 * by pulling boxes from all goals to all tiles, and filling out
	 * guaranteedDeadlocks afterwards with unvisited tiles.
	 * Before this can work however, we need a findPullablePath (from, to)
	 * @param b
	 */
	private void calculateAdditionalDeadlocks(Board b) {
		for (int i=0;i<b.getHeight();i++){
			for (int j=0;j<b.getWidth();j++){
				Coords cell = new Coords (j,i);
				if (b.getTileAt(cell) == Surf.wall || b.getTileAt(cell) == Surf.goal)
					continue;
				boolean foundPath = false;
				for (Coords c : b.getGoals()){
					String pullPath = Pathfinder.findPullablePath(c, cell, b);
					if (!pullPath.equals("")){
						foundPath = true;
						break;
					}
				}
				if (!foundPath){
					guaranteedDeadlocks[i][j] = true;
				}
			}
		}
	}
	/**
	 * Performs a walk thrhough a given path,
	 * useful mostly for debugging
	 * @param path
	 * @param board
	 */
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

	/**
	 * Checks a board for deadlock situations
	 * @param b
	 * @return
	 */
	private boolean isDeadLock(Board b) {
		Coords[] boxes = b.getBoxes();

		for (Coords c : boxes){
			if(BondHeuristics.isDeadlockSquare (c, b))
				return true;
		}
		return false;
	}

	/**
	 * Performs some mutations of a state. Tries to move a box to any goals,
	 * and moves the box one step in all possible directions.
	 * 
	 * TODO: moving the box to the goal usually does not work.
	 * A findPushablePath (from, to) is needed to make it work.
	 * Right now it finds only the shortest walkable path, and if pushing the box is unsuccessful it drops the state
	 * without trying other paths (because findPath () only finds shortest path)
	 * @param board
	 * @param inFrom
	 * @return a list of states generated by the moves performed
	 */
	private ArrayList<Board> moveBox(Board board,Coords inFrom) {
		ArrayList<Board> goalMoves = moveBoxToGoal (board, inFrom, false);
		goalMoves.addAll(moveBoxInAllDirections (board, inFrom));
		return goalMoves;
	}
	/**
	 * Tries to move a box to all goals, and returns a list of the states where this was successful
	 * @param board
	 * @param inFrom
	 * @param ignoreBoxes if boxes are to be ignored while pushing (treated as walkable)
	 * @return
	 */
	private ArrayList<Board> moveBoxToGoal(Board board, Coords inFrom, boolean ignoreBoxes) {
		Coords[] goals = board.getGoals();
		Coords from = new Coords (inFrom.x, inFrom.y);
		ArrayList<Board> boards = new ArrayList<Board> ();
		for (Coords c : goals){
			if (board.getTileAt(c) == Surf.goal){
				addState (moveBoxToCoords (from, c, board, ignoreBoxes), boards);
			}
		}
		return boards;
	}
	/**
	 * Tries to move a box to given coords.
	 * TODO:
	 * This doesn't work very well. it currently finds the shortest walkable path and tries
	 * to push the box along that path. If that fails, it gives up on it completely.
	 * We need a findPushablePath (from, to) to use this properly.
	 * @param from
	 * @param to
	 * @param board
	 * @param ignoreBoxes
	 * @return
	 */
	private Board moveBoxToCoords (Coords from, Coords to, Board board, boolean ignoreBoxes){
		String pathToPos = Pathfinder.findPushablePath (from, to, board);
		if (pathToPos == null)
			return null;
		char[] path = pathToPos.toCharArray();
		Board b = board.clone();
		for (char step : path){
			Board.Direction dir = null;
			switch (step){
			case 'U': dir = Board.Direction.UP;break;
			case 'D': dir = Board.Direction.DOWN;break;
			case 'L': dir = Board.Direction.LEFT;break;
			case 'R': dir = Board.Direction.RIGHT;break;
			}
			b = moveBox (from, dir, b, ignoreBoxes);
			if (b == null)
				return null;
			from = CoordHelper.nextCoordInDirection(dir, from);

		}
		b.modScore(20);
		return b;
	}
	/**
	 * Tries to move a box in all four directions, and saves every successful permutation state
	 * @param board
	 * @param inFrom
	 * @return all successful states resulting from the movements
	 */
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
		return moveBox(inFrom, inTo, board, false);
	}
	/**
	 * Moves a box in the given direction.
	 * Push will fail if:
	 * destination causes bipartite deadlock (guaranteedDeadlocks[dest] == true)
	 * player pushing position or destination square is not empty/goal
	 * there is no path from the player's current position to the pushing position
	 * BondHeuristics considers any box to cause a deadlock
	 * @param inFrom the box to be pushed
	 * @param inTo direction of push
	 * @param board
	 * @param disregardBoxes wether to mind any boxes when pushing 
	 * @return the state after the push, null if push failed
	 */
	private Board moveBox(Coords inFrom, Board.Direction inTo, 
			Board board, boolean disregardBoxes) {
		Coords player = board.getPlayer();
		//Copy Coords to avoid changing board state
		Coords from = new Coords (inFrom.x, inFrom.y);
		Coords to = CoordHelper.nextCoordInDirection(inTo, from);

		if (guaranteedDeadlocks[to.y][to.x])
			return null;

		Coords pushingPlayerPosition = Tools.getPushingPlayerPosition (inFrom, to);
		if (!board.isTileWalkable(pushingPlayerPosition, disregardBoxes) || !board.isTileWalkable(to, disregardBoxes))
			return null;

		String pathToPos = Pathfinder.findPath (player, pushingPlayerPosition, board, Pathfinder.WalkMode.WALK);
		if (pathToPos == null)
			return null;

		Board newBoard = board.clone();
		newBoard.modScore(board.getScore());
		String movedDirection = Tools.getMovedDirection (inTo);
		if (!newBoard.appendPath(pathToPos+movedDirection))
			return null;

		newBoard.getPlayer().x = pushingPlayerPosition.x;
		newBoard.getPlayer().y = pushingPlayerPosition.y;
		newBoard.movePlayer(inTo);
		//		if (board.getTileAt(from) == Surf.boxGoal && newBoard.getTileAt(to) == Surf.boxGoal)
		//			newBoard.modScore(-1000);

		while (BondHeuristics.tunnelPush (from, to, newBoard)){
			newBoard.movePlayer(inTo);
		}
		/*
		boolean[][] deadlockMatrix = calculateDeadlock (newBoard);
		if (deadlockMatrix[to.x][to.y])
			return null;

		 */
		if (isDeadLock (newBoard))
			return null;
		newBoard.printMap();
		return newBoard;
	}


	/**
	 * Finds a walkable path from A to B, and returns it as a series of direction chars (U,D,L,R)
	 * @param from
	 * @param to
	 * @param board
	 * @return
	 */
	public static String findPath (Coords from, Coords to, Board board){
		Stack<Coords> queue = new Stack<Coords> ();
		int[][] counterMap = new int[board.getWidth()][board.getHeight()];
		queue.push(from);
		counterMap[from.x][from.y] = 1;
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (c.equals(to))
				break;
			ArrayList<Coords> cells = walkableAdjacentCells (c, board);
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
		//p("to, x: " + to.x + " y: " + to.y);
		String bestPath = extractPath (from, to, counterMap);
		if (bestPath.equals(""))
			Tools.printCounter (counterMap, 300);
		return bestPath;
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
			return null;
		else
			return extractPath(from, new Coords(nextX, nextY), counterMap)+dir;
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
