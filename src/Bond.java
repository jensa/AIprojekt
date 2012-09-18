import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Bond implements Agent{
	
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
		//for (int i = 0; i <1; i++) {
			System.out.println(findPath (board.getPlayer(), boxes[0]));
		//}
		return "YOLO";
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
		if (counterMap[to.x+1][to.y] > 0 && counterMap[to.x+1][to.y] < nextBestScore) {
			nextX = to.x+1;
			nextY = to.y;
			nextBestScore = counterMap[nextX][nextY];
			dir = "L";
		}	
		if (counterMap[to.x-1][to.y] > 0 && counterMap[to.x-1][to.y] < nextBestScore) {
			nextX = to.x-1;
			nextY = to.y;
			nextBestScore = counterMap[nextX][nextY];
			dir = "R";
		}
		if (counterMap[to.x][to.y+1] > 0 && counterMap[to.x][to.y+1] < nextBestScore) {
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
		return extractPath(from, new Coords(nextX, nextY))+dir;
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
