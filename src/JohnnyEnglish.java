import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class JohnnyEnglish implements Agent{
	
	final int wall = 0x23;
	final int player = 0x40;
	final int playerGoal = 0x2b;
	final int box = 0x24;
	final int boxGoal = 0x2a;
	final int goal = 0x2e;
	final int empty = 0x20;

	public static void main(String[] args){
		try {
			new JohnnyEnglish ().runGames();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String solve(Board board) {
		return null;
	}
	
	public void runGames () throws IOException{
		String filePath = "d:\\sokoban\\all.slc";
		ArrayList<Board> boards = readMapFile (filePath);
		/*for (Board b : boards){
			runAGame (b);
		}*/
		runAGame (boards.get(0));
	}
	
	private void runAGame (Board board) throws IOException{
//		board.printMap();
		BoardGFX gfx = new BoardGFX ();
		gfx.initCanvas();
		gfx.drawMap(board.getBackingMatrix(), board.getPlayer().getX(), board.getPlayer().getY());
		gfx.addKeyListener(new DirectionListener (board, gfx));
	}

	public static ArrayList<Board> readMapFile (String path) throws IOException{
		ArrayList<Board> boards = new ArrayList<Board> ();
		ArrayList<String> mapRows = new ArrayList<String> ();
		BufferedReader in = new BufferedReader (new FileReader (new File (path)));
		int longestRow = 0;
		in.readLine();
		while (true){
			String line = in.readLine();
			if (line == null)
				break;
			if (line.contains(";")){
				String[] rows = new String[mapRows.size()];
				for (int i=0;i<rows.length;i++)
					rows[i] = mapRows.get(i);
				Board b = new Surf (longestRow, rows);
				boards.add(b);
				mapRows.clear();
				longestRow = 0;
				break;
			} else {
				longestRow = Math.max(longestRow, line.length());
				mapRows.add(line);
			}

		}
		return boards;
	}
	
	private class BoardGFX extends JFrame{
		
		private Graphics g;
		public final static int canvasHeight = 600;
		public final static int canvasLength = 600;
		final int cellSize = 20;
		
		public void initCanvas (){
			JPanel canvasPanel = new JPanel ();
			Canvas canvas = new Canvas ();
			canvas.setBackground(new Color(255,255,255));
			canvas.setSize(600, 600);
			canvasPanel.add(canvas);
			this.add(canvasPanel);
			this.pack();
			this.setVisible(true);
			g = canvas.getGraphics();
		}
		
		public void drawMap (char[][] matrix, int pX, int pY){
			int length = matrix[0].length;
			int height = matrix.length;
			int cellHeight = 15;
			int baseLine = 50;
			for (int i=0;i<height;i++){
				char[] row = new char[height];
				for (int j=0;j<row.length;j++){
					if (j == pX && i == pY){
						if (matrix[j][i] == Surf.goal)
							drawChar (Surf.playerGoal, j*cellSize, i*cellSize);
						else
							drawChar (Surf.player, j*cellSize, i*cellSize);
					}
					drawChar (matrix[j][i], j*cellSize, i*cellSize);
				}
				g.drawChars(row, 0, height, 50, baseLine);
				baseLine += cellHeight;
			}
		}
		
		public void clear (){
			g.clearRect(0, 0, canvasLength, canvasHeight);
		}
		
		public void drawWall (Graphics g, int x, int y, int size){
			g.drawRect(x, y, size, size);
			g.drawRect(x, y, size-2, size-2);
			g.drawRect(x, y, size-4, size-4);
			g.drawRect(x, y, size-6, size-6);
		}
		public void drawPlayer (Graphics g, int x, int y, int size){
			g.drawOval(x, y, size, size);
		}
		public void drawBox (Graphics g, int x, int y, int size){
			g.drawRect(x, y, size-1, size-1);
		}
		public void drawGoal (Graphics g, int x, int y, int size){
			g.drawOval(x, y, size, size);
			g.drawOval(x, y, size-2, size-2);
			g.drawOval(x, y, size-4, size-4);
		}
		public void drawEmpty (Graphics g, int x, int y, int size){
			Color c = g.getColor();
			g.setColor(new Color (255,255,255));
			g.fillRect(x, y, size, size);
			g.setColor(c);
		}
		public void drawUnknown (Graphics g, int x, int y, int size){
			Color c = g.getColor();
			g.setColor(new Color (0,255,255));
			g.fillRect(x, y, size, size);
			g.setColor(c);
		}
		public void drawBoxGoal (Graphics g, int x, int y, int size){
			g.drawRect(x, y, size-1, size-1);
			g.drawOval(x, y, size-2, size-2);
		}
		
		
		private void drawChar (char c, int x, int y){
			if (c == wall)
				drawWall (g,x,y, cellSize);
			else if (c == box)
				drawBox (g,x,y, cellSize);
			else if (c == goal)
				drawGoal (g,x,y, cellSize);
			else if (c == player)
				drawPlayer (g,x,y, cellSize);
			else if (c == empty)
				drawEmpty (g,x,y,cellSize);
			else if (c == boxGoal)
				drawBoxGoal (g,x,y,cellSize);
			else
				drawUnknown (g,x,y,cellSize);
		}

		public void playNextMap() {
			
		}
	}
	
	private class DirectionListener implements KeyListener{
		
		private Board b;
		private BoardGFX g;
		
		public DirectionListener (Board b, BoardGFX gx){
			this.b = b;
			g = gx;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			Board.Direction direction = null;
			switch (key){
			case KeyEvent.VK_DOWN : 
				direction = Board.Direction.DOWN;
				break;
			case KeyEvent.VK_UP : 
				direction = Board.Direction.UP;
				break;
			case KeyEvent.VK_LEFT : 
				direction = Board.Direction.LEFT;
				break;
			case KeyEvent.VK_RIGHT : 
				direction = Board.Direction.RIGHT;
				break;
			}
			if (direction != null)
				b.movePlayer(direction);
			g.clear();
			g.drawMap(b.getBackingMatrix(), b.getPlayer().getX(), b.getPlayer().getY());
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	

}
