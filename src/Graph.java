

public class Graph {
	private int vertices;
	private Edge[] edgesForth;
	private Edge[] edgesBack;

	public Graph(int verts){
		vertices = verts;
		edgesForth = new Edge[vertices+1];
		edgesBack = new Edge[vertices+1];
	}
	public void addEdgeBF(int from,int to,int cap){
		Edge lastEdgeF = edgesForth[from];
		Edge lastEdgeB = edgesBack[to];
		if(lastEdgeF == null){
			//grafen har inte sett den här noden förut
			edgesForth[from] = new Edge(from,to,cap);
		}else{
			//hitta kanten sist i länkade listan, och lägg till den nya kanten
			while(lastEdgeF.next != null){
				lastEdgeF = lastEdgeF.next;
			}
			lastEdgeF.next = new Edge(from,to,cap);
		}
		if(lastEdgeB ==null){
			edgesBack[to] = new Edge(to,from,0);
		}else{
			while(lastEdgeB.next != null){
				lastEdgeB = lastEdgeB.next;
			}
			lastEdgeB.next = new Edge(to,from,0);
		}

	}
	public Edge getEdgesForth(int vertex){
		return edgesForth[vertex];
	}
	public Edge getEdgesBack(int vertex){
		return edgesBack[vertex];
	}
	public Edge[] getEdges(){
		return edgesForth;
	}

}
