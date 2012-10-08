
public class CoordPair {
	
	public Coords from;
	public Coords to;
	public Path p;
	
	public CoordPair (Coords f, Coords t){
		this (f,t,null);
	}
	public CoordPair (Coords f, Coords t, Path pa){
		from = f;
		to = t;
		p = pa;
	}
	
	@Override
	public boolean equals (Object o){
		if (o instanceof CoordPair){
			CoordPair p = (CoordPair) o;
			if (p.from.equals(from) && p.to.equals(to))
				return true;
		}
		return false;
	}
	
	@Override
	public int hashCode (){
		return from.hashCode() + to.hashCode();
	}
	
	@Override
	public String toString (){
		return from+" -> "+to;
	}

}
