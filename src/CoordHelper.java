public class CoordHelper {
	
	public static Coords nextCoordInDirection(Surf.Direction dir, Coords from) {
		int xMod = 0; int yMod = 0;
		switch (dir){
		case UP:
			yMod = -1;
			break;
		case DOWN:
			yMod = 1;
			break;
		case LEFT:
			xMod = -1;
			break;
		case RIGHT:
			xMod = 1;
			break;
		}
		return new Coords(from.x + xMod, from.y + yMod);
	}

}
