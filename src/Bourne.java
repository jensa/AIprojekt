import java.util.Stack;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.IOException;

public class Bourne implements Agent{
    private Stack<Path> q;
    private HashSet<Coords> visited;

    public static void main(String[] args) throws IOException {
	ArrayList<Board> boards = JohnnyEnglish.readMapFile("../res/all.slc");
	Agent b = new Bourne();
	b.solve(boards.get(0));
    }

    public Bourne() {
	q = new Stack<Path>();
	visited = new HashSet<Coords>();
    }

    public String solve(Board board) {
	Coords player = board.getPlayer();
	Coords[] boxes = board.getBoxes();
	Path pathToBox = bfs(player, boxes[0], board);
	String path = pathToBox.getPath();
	moveAlongPath(path, board);
	return null;
    }

    private void moveAlongPath(String path, Board board) {
	char[] s = path.toCharArray();
	Board.Direction d = Board.Direction.UP;
	for (char c : s) {
	    switch (c) {
	    case 'U': d = Board.Direction.UP;
	    case 'D': d = Board.Direction.DOWN;
	    case 'L': d = Board.Direction.LEFT;
	    case 'R': d = Board.Direction.RIGHT;
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

	while (!q.isEmpty()) {
	    if (cc.equals(goal)) {
		return cc;
	    }

	    nc = new Coords(cc.x-1,cc.y);
	    q.push(new Path(cc, nc));
	    nc = new Coords(cc.x, cc.y-1);
	    q.push(new Path(cc, nc));
	    nc = new Coords(cc.x+1,cc.y);
	    q.push(new Path(cc, nc));
	    nc = new Coords(cc.x, cc.y+1);
	    q.push(new Path(cc, nc));

	    pc = cc;
	    cc = q.pop();
	    nc = new Coords(cc.x,cc.y);
	    while (! board.isTileWalkable(nc)) {
		cc = q.pop();
		nc = new Coords(cc.x,cc.y);
	    }

	    visited.add(new Coords(cc.x,cc.y));

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
