
public class IllegalMoveException extends Throwable{ 

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Coords bo;
	Board.Direction di;
	
	public IllegalMoveException (Coords box, Board.Direction dir){
		di = dir;
		bo = box;
	}
	
	public void printError (){
//		System.out.println ("tried to move "+bo+" in direction "+di);
	}

}
