import java.util.ArrayList;


public class MatrixTools {

	public static boolean[][] createMatrix (Board b){
		char[][] backingMatrix = b.getBackingMatrix();

		boolean[][] bipartite = new boolean[b.getHeight()][b.getWidth()];
		for (int i=0;i<b.getHeight();i++){
			for (int j=0;j<b.getWidth();j++){
				if (isDeadlockSquare (j, i, backingMatrix) )
					bipartite[i][j] = true;
			}
		}
		return bipartite;
	}

	private static boolean isDeadlockSquare(int i, int j, char[][] backingMatrix) {
		ArrayList<Coords> adjacent = createAdjacentCells (new Coords (i, j), backingMatrix);
		if (backingMatrix[i][j] == Surf.goal || backingMatrix[i][j] == Surf.boxGoal)
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

	public static ArrayList<Coords> createAdjacentCells(Coords cell, char[][] m) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		try{
			if (m[cell.x][cell.y] == Surf.wall)
				return cells;
		} catch(ArrayIndexOutOfBoundsException e){
			return cells;
		}
		addCell (new Coords (cell.x-1, cell.y), cells, m);
		addCell (new Coords (cell.x+1, cell.y), cells, m);
		addCell (new Coords (cell.x, cell.y+1), cells, m);
		addCell (new Coords (cell.x, cell.y-1), cells, m);
		return cells;
	}

	private static void addCell (Coords c, ArrayList<Coords> cells, char[][] m){
		try{
			if (m[c.x][c.y] == Surf.wall)
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

}
