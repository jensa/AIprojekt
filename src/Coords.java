
public class Coords implements Comparable<Coords>{
	
	public int x;
	public int y;
	public int id;
	public Coords par;
	
	
	public Coords (int tx, int ty){
		x = tx;
		y = ty;
		id = 0;
	}
	
	public Coords(int tx, int ty, int in) {
		x = tx;
		y = ty;
		id = in;
	}
	
	public int getX (){
		return x;
	}
	
	public int getY (){
		return y;
	}
	
	@Override public String toString() {
		return ("(" + getX() + "," + getY()+")" );
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
	
	@Override
	public int hashCode (){
		return x*1000+y;
	}

	@Override
	public int compareTo(Coords o) {
		if (o.x > x)
			return -1;
		if (o.x < x)
			return 1;
		if (o.x == x){
			if (o.y > y)
				return -1;
			if (o.y < y)
				return 1;
		}
		return 0;
	}

}
