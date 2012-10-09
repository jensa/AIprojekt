import java.util.ArrayList;


public class BondHeuristics {
	/**
	 * Figure out if the move from 'from' to 'to' was a no influence (tunnel) push
	 * 
	 * Terms:
	 *  #
	 * #$#
	 * #@#
	 * gives
	 * 	 t
	 * bl br
	 * l  r
	 * @param from
	 * @param to
	 * @param newBoard
	 * @return
	 */
	public static boolean tunnelPush(Coords from, Coords to, Board.Direction dir, Board b) {
		Coords l = null,r = null,bl = null,br = null, t = null;
		switch (dir){
		case UP: 
			l = mc(from.x-1, from.y); r = mc(from.x+1, from.y);
			bl = mc(from.x-1, from.y-1); br = mc(from.x+1, from.y-1); t = mc (from.x, from.y-2);break;
		case DOWN: 
			l = mc(from.x-1, from.y); r = mc(from.x+1, from.y);
			bl = mc(from.x-1, from.y+1); br = mc(from.x+1, from.y+1);t = mc (from.x, from.y+2);break;
		case LEFT: 
			l = mc(from.x, from.y+1); r = mc(from.x, from.y-11);
			bl = mc(from.x-1, from.y+1); br = mc(from.x-1, from.y-1);t = mc (from.x-2, from.y);break;
		case RIGHT: 
			l = mc(from.x, from.y-1); r = mc(from.x, from.y+1);
			bl = mc(from.x+1, from.y-1); br = mc(from.x+1, from.y+1);t = mc (from.x+2, from.y-2);break;
		}
		if (b.getTileAt(l) == Surf.wall && b.getTileAt(r) == Surf.wall){
			if (b.getTileAt(bl) == Surf.wall && b.getTileAt(br) == Surf.wall){
				if (b.getTileAt(t) == Surf.wall){ //box is boxed in, hopefully it wont try to do this unless there is a goal
					if( b.getTileAt(to) == Surf.goal)
						b.modScore(1000000); // box is in a place where it is always correct
					return false;
				}
			} else if (b.getTileAt(bl) == Surf.wall || b.getTileAt(br) == Surf.wall){
				if (b.getTileAt(t) != Surf.wall)
					return true;
			}
		}
		return false;
	}

	private static Coords mc (int x, int y){
		return new Coords (x, y);
	}

	public static boolean goalCorral(Coords to, Board newBoard) {
		if (newBoard.getTileAt(to) == Surf.boxGoal){
			return Tools.createAdjacentWallCells(to, newBoard).size() > 2;
		}
		return false;
	}

	public static boolean deathSquare (Coords box, Board b){
		if (b.getTileAt(box) == Surf.goal || b.getTileAt(box) == Surf.boxGoal)
			return false;
		boolean nw = isClump (getBoxCorner (-1,-1,box,b),b);
		boolean ne = isClump (getBoxCorner (1,-1,box,b),b);
		boolean sw = isClump (getBoxCorner (-1,1,box,b),b);
		boolean se = isClump (getBoxCorner (1,1,box,b),b);
		return nw || ne || sw || se;

	}

	private static ArrayList<Coords> getBoxCorner (int xMod, int yMod, Coords box, Board b){
		ArrayList<Coords> corner = new ArrayList<Coords> ();
		corner.add(new Coords(box.x+xMod, box.y+yMod));
		corner.add(new Coords(box.x, box.y+yMod));
		corner.add(new Coords(box.x+xMod, box.y));
		return corner;
	}

	private static boolean isClump (ArrayList<Coords> square, Board b){
		for (Coords c : square){
			char tile = b.getTileAt(c);
			if (!( tile== Surf.wall ||tile == Surf.boxGoal || tile == Surf.box))
				return false;
		}
		return true;
	}

	public static boolean dynamicDeadlock (Coords box, Board b){
		boolean boxCorner = isBoxesMakingCorners (box, b);
		if (boxCorner)
			return true;
		//Add more deadlock pattern recognitions ?
		return false;
	}

	private static boolean isBoxesMakingCorners(Coords box, Board b) {
		ArrayList<Coords> adjacent = Tools.createAdjacentCells (box, b);
		for (Coords c : adjacent){
			if(b.getTileAt(c) == Surf.wall){
				if (c.x == box.x){
					//wall above | under, check sides for box
					Coords left = new Coords (box.x-1, box.y);
					Coords right = new Coords (box.x+1, box.y);
					Coords lockBox = null;
					if (b.isTileAnyBox(left))
						lockBox = left;
					else if (b.isTileAnyBox(right))
						lockBox = right;
					if (lockBox != null){
						boolean isLock = isNeighbourBoxLocked (box, Board.Direction.LEFT, b);
						if (isLock)
							return true;
					}

				}else{
					// wall to the side, check top & bottom for box
					Coords up = new Coords (box.x, box.y-1);
					Coords down = new Coords (box.x, box.y+1);
					Coords lockBox = null;
					if (b.isTileAnyBox(up))
						lockBox = up;
					else if (b.isTileAnyBox(down))
						lockBox = down;
					if (lockBox != null){
						boolean isLock = isNeighbourBoxLocked (lockBox, Board.Direction.UP, b);
						if (isLock)
							return true;
					}
				}
			}
		}

		return false;
	}

	private static boolean isNeighbourBoxLocked(Coords box, Board.Direction dir,
			Board b) {
		switch (dir){
		case LEFT: //The neighbour box is located to the side of box, check up&down for walls
			if (b.isTileWall(new Coords (box.x, box.y-1)) || b.isTileWall(new Coords (box.x, box.y+1)))
				return true;
			;break;
		case UP:
			if (b.isTileWall(new Coords (box.x-1, box.y)) || b.isTileWall(new Coords (box.x+1, box.y)))
				return true;
			;break;
		}
		return false;
	}



}
