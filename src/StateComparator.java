import java.util.Comparator;

public class StateComparator implements Comparator<Board>{
	@Override
    public int compare(Board x, Board y)
    {
		int scoreX = 0;
        for (Coords box : x.getBoxes()) {
        	if (x.getTileAt(new Coords(box.x, box.y)) == Surf.boxGoal)
        			scoreX += 1;
        }
        
        int scoreY = 0;
        for (Coords box : y.getBoxes()) {
        	if (y.getTileAt(new Coords(box.x, box.y)) == Surf.boxGoal)
        			scoreY += 1;
        }
		
		// Assume neither string is null. Real code should
        // probably be more robust
        if (scoreX < scoreY)
        {
            return -1;
        }
        if (scoreX > scoreY)
        {
            return 1;
        }
        return 0;
    }

}
