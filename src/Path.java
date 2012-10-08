
public class Path {

	public Path parent;
	public char direction;
	public int size;
	
	public Path (){
		direction = '?';
		size = -1;
	}
	
	public Path (Path par,char dir){
		parent = par;
		direction = dir;
		size = -1;
	}
	
	public String toString (){
		return getPathAsString (this).toString ();
	}
	/* Maybe create our own class of backwards stringbuilderif this is slow,
	 * don't think so though
	 */
	private StringBuilder getPathAsString (Path pat){
		Path p = pat;
		StringBuilder parentChain = new StringBuilder ();
		while (p != null){
			parentChain.append(p.direction);
			p = p.parent;
		}
		parentChain.reverse();
		return parentChain;
	}
	
	public Path getRoot (){
		Path p = this;
		while (p.parent != null){
			p = p.parent;
		}
		return p;
	}
	
	public int getSize (){
		if (size > 0)
			return size;
		Path p = this;
		int siz = 1;
		while (p.parent != null){
			if (p.size > 0){
				siz+= p.size;
				break;
			}
			siz++;
			p = p.parent;
		}
		size = siz;
		return size;
	}

}
