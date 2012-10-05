
public class BondHeuristics {
	
	public static boolean isDeadlockSquare(Coords c, Board b) {
		if (b.getTileAt(c) == Surf.boxGoal)
			return false;
		return false;
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
