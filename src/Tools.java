import java.util.ArrayList;


public class Tools {
	

	public static final boolean DEBUG = false;

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

	public static void printBipartiteArray(boolean[][] bipartite, int boardHeight, int boardWidth) {
		for (int i = 0; i < boardHeight; i++) {
			for (int j = 0; j < boardWidth; j++) {
				if (bipartite[i][j]) {
					System.out.print("[x]");
				} else {
					System.out.print("[ ]");
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
	
	public static void printCounter (int[][] counterMap){
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
