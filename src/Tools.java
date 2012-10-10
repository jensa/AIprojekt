import java.util.ArrayList;


public class Tools {
	

	public static boolean DEBUG = false;

	public static boolean[][] createMatrix (Board b){
		boolean[][] bipartite = new boolean[b.getHeight()][b.getWidth()];
		for (int i=0;i<b.getHeight();i++){
			for (int j=0;j<b.getWidth();j++){
					bipartite[i][j] = isDeadlockSquare (j, i, b);
			}
		}
		calculateAdditionalDeadlocks (b.noBoxClone(), bipartite);
		return bipartite;
	}
	
	/**
	 * TODO:
	 * Intended to claculate bipartite deadlocks before starting,
	 * by pulling boxes from all goals to all tiles, and filling out
	 * guaranteedDeadlocks afterwards with unvisited tiles.
	 * Before this can work however, we need a findPullablePath (from, to)
	 * @param b
	 */
	private static void calculateAdditionalDeadlocks(Board b, boolean[][] guaranteedDeadlocks) {
		for (int i=0;i<b.getHeight();i++){
			for (int j=0;j<b.getWidth();j++){
				Coords cell = new Coords (j,i);
				if (b.getTileAt(cell) == Surf.wall || b.getTileAt(cell) == Surf.goal)
					continue;
				boolean foundPath = false;
				for (Coords c : b.getGoals()){
					Path p = Pathfinder.findPullablePath(c, cell, b);
					if (p != null){
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

	private static boolean isDeadlockSquare(int i, int j, Board b) {
		Coords c = new Coords (i, j);
		ArrayList<Coords> adjacent = createAdjacentWallCells (c, b);
		if (b.getTileAt(c) == Surf.goal || b.getTileAt(c) == Surf.boxGoal || b.getTileAt(c) == Surf.wall)
			return false;
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

	public static ArrayList<Coords> createAdjacentWallCells(Coords c, Board b) {
		ArrayList<Coords> allAdjacent = createAdjacentCells(c, b);
		for (int i=0;i<allAdjacent.size();i++){
			Coords co = allAdjacent.get(i);
			if (b.getTileAt(co) != Surf.wall){
				allAdjacent.remove(i);
				i--;
			}
		}
		return allAdjacent;
	}

	public static ArrayList<Coords> createAdjacentCells(Coords cell, Board b) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (new Coords (cell.x-1, cell.y), cells, b);
		addCell (new Coords (cell.x+1, cell.y), cells, b);
		addCell (new Coords (cell.x, cell.y+1), cells, b);
		addCell (new Coords (cell.x, cell.y-1), cells, b);
		return cells;
	}

	public static void addCell (Coords c, ArrayList<Coords> cells, Board b){
		try{
			cells.add(c);
		} catch(ArrayIndexOutOfBoundsException e){

		}
	}

	public static void printBipartiteArray(boolean[][] bipartite, 
			int boardHeight, int boardWidth) {
		System.out.print("ÿ x:");
		for ( int w = 0;w<boardWidth;w++){
			if (w<10)
				System.out.print ("[0"+w+"]");
			else
				System.out.print ("["+w+"]");
		}
		System.out.print("\n");
		for (int i = 0; i < boardHeight; i++) {
			if (i<10)
				System.out.print ("[0"+i+"]");
			else
				System.out.print ("["+i+"]");
			for (int j = 0; j < boardWidth; j++) {
				if (bipartite[i][j]) {
					System.out.print("[]");
				} else {
					System.out.print("[  ]");
				}
			}
			System.out.println("");
		}

	}
	
	public static char getMovedDirection(Board.Direction inTo) {
		switch (inTo){
		case RIGHT: return 'R';
		case LEFT: return 'L';
		case DOWN: return 'D';
		case UP: return 'U';
		}
		return '@';
	}
	
	public static Coords getPushingPlayerPosition(Coords inFrom, Coords inTo) {
		int x = inFrom.x;
		int y = inFrom.y;
		int modX = inFrom.x - inTo.x;
		int modY = inFrom.y - inTo.y;
		return new Coords (x + modX, y+modY);
	}
	
	public static Board.Direction getDirection(Coords from, Coords to) {
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
	
	public void printBoxes (ArrayList<Coords> bx){
		int kuk = 1;
		for (Coords lol : bx){
			p ("Box "+kuk+": "+lol);
			kuk++;
		}
	}
	
	public void p (String m){
		if (DEBUG)
			p (m);
	}
	public static void printCounter (int[][] counterMap, int sleep){
		printCounter (counterMap, sleep, false);
	}
	
	public static void printCounter (int[][] counterMap, int sleep, boolean debug){
		if (debug){
			int width = counterMap.length;
			int height = counterMap[0].length;
			for (int i=0;i<height;i++){
				for (int j=0;j<width;j++){
					if (counterMap[j][i] > 9 || counterMap[j][i] < 0){
						System.out.print("["+counterMap[j][i]+"]");
					}else
						System.out.print("[0"+counterMap[j][i]+"]");

				}
				System.out.print("\n");
			}
			System.out.println ("-----------------");
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Performs a walk thrhough a given path,
	 * useful mostly for debugging
	 * @param path
	 * @param board
	 */
	public static void doWalk(String path, Board board) {
		char[] pathc = path.toCharArray();
		for (char step : pathc){
			Board.Direction dir = null;
			switch (step){
			case 'U': dir = Board.Direction.UP;break;
			case 'D': dir = Board.Direction.DOWN;break;
			case 'L': dir = Board.Direction.LEFT;break;
			case 'R': dir = Board.Direction.RIGHT;break;
			}
			try {
				board.movePlayer(dir);
			} catch (IllegalMoveException e1) {
				e1.printError();
			}
			board.printMap();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
