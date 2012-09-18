
public class Coords {
	
	public int x;
	public int y;
	
	public Coords (int tx, int ty){
		x = tx;
		y = ty;
	}
	
	public int getX (){
		return x;
	}
	
	public int getY (){
		return y;
	}
	
	@Override public String toString() {
		return ("coords: " + getX() + " " + getY() );
	}
	
	@Override
	public boolean equals (Object o){
		boolean isEqual = false;
		if (o instanceof Coords){
			Coords c = (Coords) o;
			isEqual = c.getX() == x && c.getY() == y;
		}
		return isEqual;
	}

}
