import java.util.ArrayList;
import java.util.Arrays;

/**
 * Looks like shit, copied from my ADK Ford-Fulkerson lab solution.
 * @author jens
 *
 */

public class Matcher {

	public static CoordPair[] getMatch (Board b) throws Exception{
		if (b == null)
			return null;
		//		b.printMap();
		ArrayList<Integer> edgesA = new ArrayList<Integer> ();
		ArrayList<Integer> edgesB = new ArrayList<Integer> ();
		Coords[] goals = getFreeItem (b.getGoals(), Surf.goal,b);
		Coords[] boxes = getFreeItem (b.getBoxes(), Surf.box, b);

		int[] boxHasEdge = new int[boxes.length];
		int[] goalHasEdge = new int[goals.length];
		ArrayList<Path> capz = new ArrayList<Path> ();
		Arrays.fill(boxHasEdge, -1);Arrays.fill(goalHasEdge, -1);
		int numBoxes = 0;
		int numGoals = 0;
		for (int i= 0;i<boxes.length;i++){
			Coords bo = boxes[i];
			if (Pathfinder.findWalkablePath(b.getPlayer(), bo, b) == null){
				continue;
			}
			for (int j = 0;j<goals.length;j++){
				Coords go = goals[j];
				Path p = Pathfinder.findPushablePath (bo,go,b);
				if (p != null){
					if (boxHasEdge[i] < 0){
						numBoxes++;
						boxHasEdge[i] = numBoxes-1;
					}
					if (goalHasEdge[j] < 0){
						numGoals++;
						goalHasEdge[j] = numGoals-1;
					}
					edgesA.add(boxHasEdge[i]);
					edgesB.add(goalHasEdge[j]);
					capz.add(p);
				}
			}
		}
		Coords[] boxVerts = new Coords[numBoxes];
		Coords[] goalVerts = new Coords[numGoals];
		for (int i=0;i<boxes.length;i++){
			if (boxHasEdge[i] >= 0)
				boxVerts[boxHasEdge[i]] = boxes[i];

		}
		for (int i=0;i<goals.length;i++){
			if (goalHasEdge[i] >= 0)
				goalVerts[goalHasEdge[i]] = goals[i];
		}
		int numVerts = numBoxes+numGoals+2;
		int numEdges = edgesA.size()+numVerts-2;
		if (numEdges < 1)
			return null;
		int[] froms = new int[numEdges];
		int[] tos = new int[numEdges];
		int src = 1;
		int sink = numVerts;
		int edgeIndex = 0;

		for (int i=2;i<=numBoxes+1;i++, edgeIndex++){
			froms[edgeIndex] = src;
			tos[edgeIndex] = i;
		}
		for (int i = 0;i<edgesA.size();i++, edgeIndex++){
			froms[edgeIndex] = edgesA.get(i)+2; // +2 because index starts at 0, first
			tos[edgeIndex] = edgesB.get(i)+numBoxes+2; // non-sink node is numbered 2 in graph
		}

		for (int i=numBoxes+2;i<numVerts;i++, edgeIndex++){
			froms[edgeIndex] = i;
			tos[edgeIndex] = sink;
		}
		int[] caps = new int[numEdges];
		Arrays.fill(caps, 1);
		MaxFlow maxFlow = new MaxFlow ();
		Graph g = maxFlow.run(froms, tos, caps,numVerts, src, sink, numEdges);
		return getSolution (g, numEdges, src, sink, goals, boxes, edgesA, edgesB, boxVerts, goalVerts, b);




	}

	private static Coords[] getFreeItem(Coords[] items, char comp, Board b) {
		int freeItemLength = 0;
		for (Coords c : items){
			if (b.getTileAt(c) == comp)
				freeItemLength++;
		}
		Coords[] freeItems = new Coords[freeItemLength];
		for (int i=0,j=0; i<items.length;i++){
			Coords c = items[i];
			if (b.getTileAt(c) == comp){
				freeItems[j] = c;
				j++;
			}

		}
		return freeItems;
	}

	private static CoordPair[] getSolution (Graph g, int numEdges, 
			int src, int sink, Coords[] goals, Coords[] boxes, 
			ArrayList<Integer> edgesA, ArrayList<Integer> edgesB, 
			Coords[] boxVerts, Coords[] goalVerts, Board b){
		int matches = 0;
		Edge srcEdges = g.getEdgesForth(src);
		while(srcEdges != null){
			Edge e = srcEdges;
			matches += e.flow;
			srcEdges = srcEdges.next;
		}
		CoordPair[] match = new CoordPair[matches];
		Edge[] allVertices = g.getEdges();
		int posEdgeIndex = 0;
		for(Edge vertexEdges : allVertices){ 
			while(vertexEdges != null){
				Edge e = vertexEdges;
				if(e.flow>0){
					if (!(e.from == src || e.to == sink || e.from == sink || e.to == src)){
						CoordPair edge = new CoordPair(boxVerts[e.from-2], goalVerts[e.to-2-boxVerts.length]);
						edge.p = Pathfinder.findPushablePath(edge.from, edge.to, b);
						match[posEdgeIndex] = edge;
						posEdgeIndex++;
					}
				}
				vertexEdges = vertexEdges.next;
			}


		}
		return match;
	}

}
