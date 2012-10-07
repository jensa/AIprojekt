import java.util.ArrayList;


public class Tools {
	

	public static boolean DEBUG = false;

	public static boolean[][] createPullMatrix(Board b) {
		boolean[][] returnMatrix = new boolean[b.getHeight()][b.getWidth()];
		Coords[] goals = b.getGoals();
		for (int i = 0; i < goals.length; i++) {
			Coords goal = goals[i];
			returnMatrix = pull(returnMatrix, goals[i], b);
			
		}
		return returnMatrix;
	}
	
	private static boolean[][] pull(boolean[][] m, Coords start, Board b) {
		m[start.x][start.y] = true; 
		Coords c1 = new Coords(start.x+1, start.y);
		Coords c2 = new Coords(start.x+2, start.y);
		Coords c3 = new Coords(start.x-1, start.y);
		Coords c4 = new Coords(start.x-2, start.y);
		Coords c5 = new Coords(start.x, start.y+1);
		Coords c6 = new Coords(start.x, start.y+2);
		Coords c7 = new Coords(start.x, start.y-1);
		Coords c8 = new Coords(start.x, start.y-2);
		
		Coords c = c1;
		Coords n = c2;
		if (!m[c.x][c.y] && canWalkOn(b, c) && canWalkOn(b, n)) {
			m[c.x][c.y] = true;
			Tools.printBipartiteArray(m, m.length, m[0].length);
			m = pull(m, c, b);
		}
		c = c3;
		n = c4;
		if (!m[c.x][c.y] && canWalkOn(b, c) && canWalkOn(b, n)) {
			m[c.x][c.y] = true;
			Tools.printBipartiteArray(m, m.length, m[0].length);
			m = pull(m, c, b);
		}
		c = c5;
		n = c6;
		if (!m[c.x][c.y] && canWalkOn(b, c) && canWalkOn(b, n)) {
			m[c.x][c.y] = true;
			Tools.printBipartiteArray(m, m.length, m[0].length);
			m = pull(m, c, b);
		}
		c = c7;
		n = c8;
		if (!m[c.x][c.y] && canWalkOn(b, c) && canWalkOn(b, n)) {
			m[c.x][c.y] = true;
			Tools.printBipartiteArray(m, m.length, m[0].length);
			m = pull(m, c, b);
		}
		return m;
	}
	
	private static boolean canWalkOn(Board b, Coords i) {
		char tmpTile = b.getTileAt(i);
		if (tmpTile == Surf.box || tmpTile == Surf.boxGoal || tmpTile == Surf.empty || tmpTile == Surf.goal || tmpTile == Surf.player || tmpTile == Surf.playerGoal) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean[][] createMatrix (Board b){
		boolean[][] bipartite = new boolean[b.getHeight()][b.getWidth()];
		for (int i=0;i<b.getHeight();i++){
			for (int j=0;j<b.getWidth();j++){
					bipartite[i][j] = isDeadlockSquare (j, i, b);
			}
		}
		return bipartite;
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
		System.out.print("� x:");
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
					System.out.print("[XX]");
				} else {
					System.out.print("[  ]");
				}
			}
			System.out.println("");
		}

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
	
	public static Coords getPushingPlayerPosition(Coords inFrom, Coords inTo) {
		int x = inFrom.x;
		int y = inFrom.y;
		int modX = inFrom.x - inTo.x;
		int modY = inFrom.y - inTo.y;
		return new Coords (x + modX, y+modY);
	}
	
	public static Board.Direction getDirection(Coords from, Coords to) {
		// de m�ste vara en ifr�n
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

}
