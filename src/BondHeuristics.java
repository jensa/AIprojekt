
public class BondHeuristics {
	
	public static boolean isDeadlockSquare(Coords c, Board b) {
		if (b.getTileAt(c) == Surf.boxGoal)
			return false;
		if (boxAgainstGoallessWall (c, b.getBackingMatrix()))
			return true;
		return false;
	}
	
	private static boolean boxAgainstGoallessWall (Coords c, char[][] backingMatrix){
		boolean leftWall = wall (Board.Direction.LEFT, c, backingMatrix);
		if (leftWall){
			if (!goalInColumn (c, backingMatrix) && c.x == 1){
				return true;
			}
		}
		boolean rightWall = wall (Board.Direction.RIGHT, c, backingMatrix);
		if (rightWall){
			if (!goalInColumn (c, backingMatrix) && c.x == backingMatrix.length-2){
				return true;
			}
		}
		boolean upWall = wall (Board.Direction.UP, c, backingMatrix);
		if (upWall){
			if (!goalInRow (c, backingMatrix) && c.y == 1){
				return true;
			}
		}
		boolean downWall = wall (Board.Direction.DOWN, c, backingMatrix);
		if (downWall){
			if (!goalInRow (c, backingMatrix) && c.y == backingMatrix[0].length-2){
				return true;
			}
		}
		return false;
	}
	
	private static boolean goalInRow(Coords c, char[][] backingMatrix) {
		for (int i=0;i<backingMatrix.length-1;i++){
			if (backingMatrix[i][c.y] == Surf.boxGoal || backingMatrix[i][c.y] == Surf.goal)
				return true;
		}
		return false;
	}

	private static boolean goalInColumn(Coords c, char[][] backingMatrix) {
		try{
		for (int i=0;i<backingMatrix[0].length-1;i++){
			if (backingMatrix[c.x][i] == Surf.boxGoal || backingMatrix[c.x][i] == Surf.goal)
				return true;
		}
		} catch (Exception e){
			int lol= 0;
			lol = lol+1;
		}
		return false;
	}

	private static boolean wall(Board.Direction dir, Coords c, char[][] b) {
		char[] tiles = new char[3];
		switch (dir){
		case RIGHT:
			tiles[0]=b[c.x+1][c.y-1];tiles[1]= b[c.x+1][c.y]; tiles[2]= b[c.x+1][c.y+1];break;
		case LEFT:
			tiles[0]=b[c.x-1][c.y-1];tiles[1]= b[c.x-1][c.y]; tiles[2]= b[c.x-1][c.y+1];break;
		case UP:
			tiles[0]=b[c.x-1][c.y-1];tiles[1]= b[c.x][c.y-1]; tiles[2]= b[c.x+1][c.y-1];break;
		case DOWN:
			tiles[0]=b[c.x-1][c.y+1];tiles[1]= b[c.x][c.y+1]; tiles[2]= b[c.x+1][c.y+1];break;
		}
		return isTilesWalls (tiles);
	}
	
	private static boolean isTilesWalls(char[] tiles) {
		for (char c : tiles)
			if (c != Surf.wall)
				return false;
		return true;
	}
	
	/**
	 * Figure out if the move from 'from' to 'to' was a no influence (tunnel) push
	 * @param from
	 * @param to
	 * @param newBoard
	 * @return
	 */
	public static boolean tunnelPush(Coords from, Coords to, Board newBoard) {
		
		return false;
	}

}
