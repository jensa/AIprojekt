package flowMatch;
public class Edge{
		public int flow;
		public int cap;

		public int from;
		public int to;
		
		public Edge next;

		public Edge(int from, int to,int cap){
			this.from = from;
			this.to = to;
			this.cap = cap;
		}
		public void addEdge(Edge e){
			next = e;
		}
	}
