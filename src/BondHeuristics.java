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



}
