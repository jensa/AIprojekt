

import java.util.ArrayList;
import java.util.Arrays;

public class MaxFlow {


	private Graph g;

	Kattio io;
	private int numberOfVertices;
	private int src;
	private int sink;
	private int numberOfEdges;

	private Queue q;
	

	public MaxFlow() throws Exception{
	}
	public Graph run(int[] froms,int[] tos,int[] caps,int verts, int src,int sink,int edges) throws Exception{
		this.sink = sink;
		this.src = src;
		this.numberOfEdges= edges;
		this.numberOfVertices = verts;
		g = new Graph(numberOfVertices);
		for(int i=0;i<numberOfEdges;i++){
			int from = froms[i];
			int to = tos[i];
			int cap = caps[i];
			g.addEdgeBF(from, to, cap);
		}
		edmondsKarp();
		return g;
	}

	public void edmondsKarp() throws Exception{
		ArrayList<Integer> path = breadthFirst();
		while(path != null){
			//Find min flow in path
			int minFlow = minFlow(path);
			//add flow & decrease rest capacity (& 'increase' the 'backwards' rest capacity)
			alterFlow(minFlow,path);
			path = breadthFirst();
		}

	}
	private ArrayList<Integer> breadthFirst() throws Exception {
		q = new Queue();
		ArrayList<Integer> path = new ArrayList<Integer>();
		while(true){
			q.Put(src);
			int[] cameFrom = new int[numberOfVertices+1];
			boolean[] visited = new boolean[numberOfVertices+1];
			Arrays.fill(cameFrom,Integer.MIN_VALUE);
			visited[src] = true;
			boolean pathFound = false;
			while(!q.IsEmpty() && !pathFound){
				int curr = (Integer) q.Get();
				visited[curr] = true;
				Edge edgesF = g.getEdgesForth(curr);
				Edge edgesB = g.getEdgesBack(curr);
				//Check if this node's edges has rest capacity
				//and hasn't been visited before this search
				pathFound = findValidEdges(cameFrom,visited,edgesF,curr,1);
				if(!pathFound){
					//Check back edges aswell
					pathFound = findValidEdges(cameFrom,visited,edgesB,curr,-1);
				}
			}
			if(pathFound){
				int vertNo = sink;
				while(Math.abs(vertNo) != src){
					path.add(vertNo);
					vertNo = cameFrom[Math.abs(vertNo)];
				}
				path.add(src);
				return path;
			}else{
				return null;
			}
		}
	}
	//Returns true if sink was found
	private boolean findValidEdges(int[] cameFrom,boolean[] visited,Edge edges,int curr,int backEdge){
		while(edges != null){
			Edge e = edges;
			int restCap = e.cap-e.flow;
			//Finns restkapacitet? har vi varit här? is this real life?
			if(restCap > 0 && !visited[e.to]){
				if(e.to == sink){
					cameFrom[sink] = curr;
					return true;
				}else{
					//Did we already go to this node?
					if(cameFrom[e.to]<0){
						//Negative value if this is an edge 'backwards'
						cameFrom[e.to] = curr*backEdge;
					}
					q.Put(e.to);
				}
			}
			edges = edges.next;
		}
		return false;
	}

	private Edge getEdgeTo(int vert,Edge edges){
		try{
			while(vert != edges.to){
				edges = edges.next;
			}
			return edges;
		}catch(NullPointerException e){
			return null;
		}
	}



	private int minFlow(ArrayList<Integer> p){
		int minFlow = Integer.MAX_VALUE;
		for(int i=p.size()-1;i>=2-1;i--){
			int from = Math.abs(p.get(i));
			int to = Math.abs(p.get(i-1));
			Edge e = getEdgeTo(to,g.getEdgesForth(from));
			if(e == null){
				e = getEdgeTo(to,g.getEdgesBack(from));
			}
			int restCap = e.cap-e.flow;
			if(restCap<minFlow){
				minFlow = restCap;
			}

		}
		return minFlow;
	}

	private void alterFlow(int flow,ArrayList<Integer> path){
		for(int i=0;i<path.size()-1;i++){
			int vertTo = Math.abs(path.get(i));
			int vertFrom = Math.abs(path.get(i+1));
			Edge e = getEdgeTo(vertTo,g.getEdgesForth(vertFrom));
			if(e == null){
				e = getEdgeTo(vertTo,g.getEdgesBack(vertFrom));
			}
			e.flow += flow;
			Edge eBack = getEdgeTo(vertFrom,g.getEdgesBack(vertTo));
			if(eBack == null){
				eBack = getEdgeTo(vertFrom,g.getEdgesForth(vertTo));
			}
			eBack.flow -= flow; 
		}
	}

}
