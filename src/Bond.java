public class Bond implements Agent{

	@Override
	public String solve(Board board) {
		System.out.println("solve");
		Coords[] boxes = board.getBoxes();
		board.printMap();
		board.movePlayer(Board.Direction.RIGHT);
		board.printMap();
		for (int i = 0; i < boxes.length; i++) {
			System.out.println(boxes[i].toString());
		}
		return "YOLO";
	}

}
