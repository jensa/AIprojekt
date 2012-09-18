import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import SokobanBoard.Cell;

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
		board.movePlayer(Board.Direction.RIGHT);
		board.printMap();
		for (int i = 0; i < boxes.length; i++) {
			System.out.println(boxes[i].toString());
		}
		return "YOLO";
	}
	
	public String findPath (Coords from, Coords to){
		Stack<Coords> queue = new Stack<Coords> ();
		counterMap = new int[boardWidth][boardHeight];
		queue.push(from);
		while (!queue.isEmpty()){
			Coords c = queue.pop();
			if (cell.equals(start)){
				break;
			}
			ArrayList<Coords> cells = createAdjacentCells (cell);
			for (int i=0; i<cells.size();i++){
				Cell adjacentCell = cells.get(i);
				boolean lowerCounterExists = counterMap [adjacentCell.x][adjacentCell.y] > 0 && 
						counterMap [adjacentCell.x][adjacentCell.y] < adjacentCell.counter;
				if (lowerCounterExists){
					cells.remove(i);
					i--;
				}
				else{
					counterMap [adjacentCell.x][adjacentCell.y] = adjacentCell.counter;
				}
			}
			for (Cell c : cells)
				cellQueue.addFirst(c);
		}
		return extractPath ();
	}
	
	private ArrayList<Coords> createAdjacentCells(Coords cell) {
		ArrayList<Coords> cells = new ArrayList<Coords> ();
		addCell (cell.x-1, cell.y, cell.counter+1, cells);
		addCell (cell.x+1, cell.y, cell.counter+1, cells);
		addCell (cell.x, cell.y+1, cell.counter+1, cells);
		addCell (cell.x, cell.y-1, cell.counter+1, cells);
		return cells;
	}
	
	private void addCell (Coords c, ArrayList<Coords> cells){
		if (board.isTileWalkable(c))
	}
	
	
	
	
	

}
