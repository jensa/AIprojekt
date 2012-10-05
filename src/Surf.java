import java.util.ArrayList;


public class Surf implements Board{

	private String path;
	final int height;
	final int width;

	private int scoreMod;

	int nr = 0;

	public static final char wall = 0x23;
	public static final char player = 0x40;
	public static final char playerGoal = 0x2b;
	public static final char box = 0x24;
	public static final char boxGoal = 0x2a;
	public static final char goal = 0x2e;
	public static final char empty = 0x20;
	public static final char OOB = 0x30;

	public char[][] boardMatrix;
	public ArrayList<Coords> goals;
	public Coords playerPosition;
	public ArrayList<Coords> boxes;

	private static int lol;


	public Surf (int longestRow, String[] rows){
		path = "";
		boxes = new ArrayList<Coords> ();
		goals = new ArrayList<Coords> ();
		height = rows.length;
		width = longestRow;
		boardMatrix = new char[width][height];

		for (int i=0;i<rows.length;i++){
			addRow (rows[i], i);
		}
	}
	
	public Surf(Board b, boolean noBoxes) {
		goals = (ArrayList<Coords>) b.getGoalsList().clone();
		path = b.getPath();
		boxes = new ArrayList<Coords> ();
		for (Coords c : b.getBoxHash())
			boxes.add(new Coords (c.x, c.y));
		width = b.getWidth();
		height = b.getHeight();
		boardMatrix = new char[width][height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				if (b.getBackingMatrix()[i][j] == boxGoal || b.getBackingMatrix()[i][j] == box)
					this.boardMatrix[i][j] = empty;
				else
					this.boardMatrix[i][j] = b.getBackingMatrix()[i][j];
			}
		}
		this.playerPosition = new Coords(b.getPlayer().x, b.getPlayer().y);
	}

	public Surf(Board b) {
		goals = (ArrayList<Coords>) b.getGoalsList().clone();
		path = b.getPath();
		boxes = new ArrayList<Coords> ();
		for (Coords c : b.getBoxHash())
			boxes.add(new Coords (c.x, c.y));
		width = b.getWidth();
		height = b.getHeight();
		boardMatrix = new char[width][height];
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				this.boardMatrix[i][j] = b.getBackingMatrix()[i][j];
			}
		}
		this.playerPosition = new Coords(b.getPlayer().x, b.getPlayer().y);
	}

	@Override
	public Board clone() {
		try {
			Board newBoard = new Surf(this);
			return newBoard;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Board noBoxClone (){
		return new Surf (this, true);
		
	}

	private void addRow (String row, int rowNum){

		for (int i=0;i<row.length();i++){
			char currentTile = row.charAt(i);
			boardMatrix[i][rowNum] = currentTile;
			if (currentTile == playerGoal){
				playerPosition = new Coords (i, rowNum);
				boardMatrix[i][rowNum] = goal;
				currentTile = goal;
			}
			if (currentTile == player){
				playerPosition = new Coords (i, rowNum);
				boardMatrix[i][rowNum] = empty;
				currentTile = empty;
			}
			if (currentTile == goal || currentTile == boxGoal){
				goals.add(new Coords (i, rowNum));
			}
			if (currentTile == box) {
				boxes.add(new Coords (i, rowNum, nr));
				nr += 1;
			}
			if (currentTile == boxGoal) {
				boxes.add(new Coords(i, rowNum, nr));
				nr += 1;
			}
		}
	}

	@Override
	public void movePlayer(Direction dir) {
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
		Coords newPos = new Coords (playerPosition.getX()+xMod, playerPosition.getY()+yMod);
		boolean emptyPosition = isTileWalkable (newPos);
		if (emptyPosition){
			doMove (newPos);
		}
		if (isTileBox (newPos)){
			Coords nextTile = new Coords (newPos.getX()+xMod, newPos.getY()+yMod);
			if (isTileWalkable (nextTile) )
				doBoxMove (newPos, nextTile);
		}
	}

	private void doMove(Coords newPos) {
		playerPosition = newPos;
	}

	private void doBoxMove(Coords boxPos, Coords moveTo) {
		if (isTileGoal (boxPos))
			boardMatrix[boxPos.getX()][boxPos.getY()] = goal;
		else
			boardMatrix[boxPos.getX()][boxPos.getY()] = empty;
		if (isTileGoal (moveTo))
			boardMatrix[moveTo.getX()][moveTo.getY()] = boxGoal;
		else
			boardMatrix[moveTo.getX()][moveTo.getY()] = box;
		playerPosition = boxPos;
		updateBox (boxPos, moveTo);
	}

	private void updateBox(Coords from, Coords to) {
		for (int i=0;i<boxes.size();i++){
			if (boxes.get(i).equals(from)){
				boxes.get(i).x = to.x;
				boxes.get(i).y = to.y;
				break;
			}
		}
	}

	@Override
	public boolean equals (Object o){
		if (!(o instanceof Board))
			return false;
		Board b = (Board) o;
		if (b.getHeight() != getHeight () || b.getWidth() != getWidth ())
			return false;
		for (int i=0;i< b.getWidth();i++){
			for (int j=0;j<b.getHeight();j++){
				Coords c = new Coords(i,j);
				if (b.getTileAt(c) != getTileAt(c))
					return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode (){
		
		int hash = 0;
		for (int i=0;i< getWidth();i++){
			for (int j=0;j<getHeight();j++){
				hash += getTileAt (new Coords (j,i));
			}
		}
		return hash;
	}

	private boolean isTileGoal(Coords c) {
		if (goals.contains(c))
			return true;
		return false;
	}

	private boolean isTileBox (Coords c){
		if (boardMatrix[c.getX()][c.getY()] == box | boardMatrix[c.getX ()][c.getY()] == boxGoal)
			return true;
		return false;
	}

	@Override
	public char getTileAt(Coords c) {
		try{
			return boardMatrix[c.x][c.y];
		}catch (IndexOutOfBoundsException e){
			return OOB; 
		}

	}	
	public boolean isTileWalkable(Coords c, boolean ignoreBoxes) {
		if (ignoreBoxes){
			char[] chars = {empty, goal, box, boxGoal};
			return checkLocationForTiles (chars, c);
		}else{
			char[] chars = {empty, goal};
			return checkLocationForTiles (chars, c);
		}
	}
	@Override
	public boolean isTileWalkable(Coords c) {
		char[] chars = {empty, goal};
		return checkLocationForTiles (chars, c);
	}

	private boolean checkLocationForTiles (char[] chars, Coords c){
		for (char ch : chars){
			if (getTileAt (c) == ch)
				return true;
		}
		return false;
	}
	@Override
	public Coords[] getGoals() {
		Coords[] gols = new Coords[goals.size()];
		return goals.toArray(gols);
	}

	@Override
	public Coords getPlayer() {
		return playerPosition;
	}

	@Override
	public Coords[] getBoxes() {
		//Coords[] boxz = new Coords[boxes.size()];
		//return goals.toArray(boxz);
		Coords[] boxz = new Coords[boxes.size()];
		boxes.toArray(boxz);
		return boxz;
	}

	public boolean isSolved (){
		for (Coords c : boxes){
			if ( !goals.contains(c) )
				return false;
		}
		return true;
	}

	public void printMap (){
		lol = 1;
		if (lol>1)
			return;

		for (int i=0;i<height;i++){
			for (int j=0;j<width;j++)
				if (new Coords(j,i).equals(this.playerPosition))
					System.out.print("[@]");
				else
					System.out.print("["+boardMatrix[j][i]+"]");
			System.out.print("\n");
		}
		System.out.println (getScore ());
		System.out.println("-----------------------");
		if (Tools.DEBUG){
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public char[][] getBackingMatrix (){
		return boardMatrix;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getWidth() {
		return width;
	}

	public ArrayList<Coords> getBoxHash() {
		return boxes;
	}

	@Override
	public ArrayList<Coords> getGoalsList() {
		return this.goals;
	}

	@Override
	public String hash() {
		Coords[] b = new Coords[boxes.size()]; 
		for (int i=0; i<boxes.size();i++)
			b[i] = boxes.get(i);
		/*
		for (int i = 0; i < b.length - 1; i++) {
			double b1 =b[i].x*100;
			double a = b[i].y*1;
			double c = (a + b1)*(Math.pow(1000,b[i].id));
			hash += c;
		}*/
		StringBuilder builder = new StringBuilder(b.length*4);
		for (Coords c : b){
			builder.append(""+c.x+c.y);
		}
		return builder.toString();
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public boolean appendPath(String pathPart) {
		path = path.concat(pathPart);
		return true;


	}
	/**
	 * Score is calced the following:
	 * box on goal: 10pts
	 * path to goal form box: 1pt
	 */
	public int getScore (){
		int kuk = 0;
		for (Coords c : goals){
			if (getTileAt (c) == boxGoal)
				kuk += 10;
		}
		return kuk+scoreMod;

	}

	public void modScore (int mod){
		scoreMod += mod;
	}

	@Override
	public int compareTo(Board o) {
		if (o.getScore() > getScore ())
			return -1;
		else if (o.getScore() < getScore ())
			return 1;
		return 0;
	}
}
