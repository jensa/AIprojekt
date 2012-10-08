
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

}
