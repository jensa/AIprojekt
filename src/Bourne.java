import java.util.Stack;
import java.util.ArrayList;
import java.io.IOException;

public class Bourne implements Agent{
    private Stack<Path> q;
    private boolean[][] visited;

    public static void main(String[] args) throws IOException {
    	new Bourne();
    }

    public Bourne() throws IOException {
    
    ArrayList<Board> boards = JohnnyEnglish.readMapFile("../res/all.slc");
    
    Board tempBoard = boards.get(0);
        	
	q = new Stack<Path>();
	visited = new boolean[tempBoard.getHeight()][tempBoard.getWidth()];
    
	solve(tempBoard);
    }

    public String solve(Board board) {
    	
    System.out.println("Printing map..");
	board.printMap();
    System.out.println("Locating player..");
	Coords player = board.getPlayer();
    System.out.println("Locating boxes..");
	Coords[] boxes = board.getBoxes();
    System.out.println("Finding path to box 0..");
	Path pathToBox = bfs(player, boxes[0], board);
	
	
	String path = pathToBox.getPath();
	
	moveAlongPath(path,board);
	
	return null;
    }

    private void moveAlongPath(String path, Board board) {
	char[] s = path.toCharArray();
	Board.Direction d = Board.Direction.UP;
	for (char c : s) {
	    
		switch (c) {
		    case 'U': {d = Board.Direction.UP; break;}
		    case 'D': {d = Board.Direction.DOWN; break;}
		    case 'R': {d = Board.Direction.RIGHT; break;}
		    case 'L': {d = Board.Direction.LEFT; break;}
	    }
	    
	    board.movePlayer(d);
	    board.printMap();
	}
    }

    private Path bfs(Coords start, Coords goal, Board board) {
    	
	Path pc = new Path(null,start);
	Path cc = new Path(null,start);
	Coords nc = new Coords(cc.x,cc.y);
	
	q.push(cc);
	
	visited[cc.y][cc.x] = true;
	
	while (!q.isEmpty()) {
		
	    if (cc.isAdjacentTo(goal)) {
	    	System.out.println("Goal found");
	    	return cc;
	    }

	    nc = new Coords(cc.x-1,cc.y);
	    if (board.isTileWalkable(nc) && !visited[nc.x][nc.y]){
	    	System.out.println("Adding " + nc.x + "," + nc.y + " to the queue");
	    	visited[nc.x][nc.y] = true;
	    	q.push(new Path(cc, nc));
	    }
	    
	    nc = new Coords(cc.x, cc.y-1);
	    if (board.isTileWalkable(nc) && !visited[nc.x][nc.y]){
	    	System.out.println("Adding " + nc.x + "," + nc.y + " to the queue");
	    	visited[nc.x][nc.y] = true;
	    	q.push(new Path(cc, nc));
	    }
	    
	    nc = new Coords(cc.x+1,cc.y);
	    if (board.isTileWalkable(nc) && !visited[nc.x][nc.y]){
	    	System.out.println("Adding " + nc.x + "," + nc.y + " to the queue");
	    	visited[nc.x][nc.y] = true;
	    	q.push(new Path(cc, nc));
	    }
	    
	    nc = new Coords(cc.x, cc.y+1);
	    if (board.isTileWalkable(nc) && !visited[nc.x][nc.y]){
	    	System.out.println("Adding " + nc.x + "," + nc.y + " to the queue");
	    	visited[nc.x][nc.y] = true;
	    	q.push(new Path(cc, nc));
	    }

	    pc = cc;
	    cc = q.pop();
	    nc = new Coords(cc.x,cc.y);
	}
	return null;
    }

    private class Path {
	private Path parent;
	public int x,y;

	public Path (Path parent, Coords c) {
	    this.parent = parent;
	    this.x = c.x;
	    this.y = c.y;
	}

	public boolean isAdjacentTo(Coords goal) {
		if (this.y == goal.y){
			if (this.x == goal.x-1 || this.x == goal.x+1){
				return true;
			}
		}
		
		if (this.x == goal.x){
			if (this.y == goal.y-1 || this.y == goal.y+1){
				return true;
			}
		}
		
		return false;
	}

	public boolean equalsCoords(Coords c) {
	    if (c.x == x && c.y == y) return true;
	    return false;
	}
	
	public String getPath() {
	    if (parent == null) return "";
	    else {
		System.out.println(x +","+y+" Dir: " +determineDir());
		String s = parent.getPath() + determineDir();
		return s;
	    }
	}

	private String determineDir() {
	    if (x > parent.x) return "R";
	    else if (y < parent.y) return "U";
	    else if (y > parent.y) return "D";
	    else if (x < parent.x) return "L";
	    else return "";
	}
    }


}
