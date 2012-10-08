
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class BipReduce {
	Kattio io;
	private int graphX;
	private int graphY;
	private int numVertices;
	private int numEdges;
	private int src;
	private int sink;
	
	static boolean isHome =false;
	static String dataPath = "C:\\Users\\jensarv\\Desktop\\Eclipseprojekt\\flowMatch\\data\\";
	
	int[] edgesA;
	int[] edgesB;
	int[] edgesCap;
	private Graph g;

	public static void main(String[] args){
		new BipReduce();

	}
	public BipReduce(){
		if(isHome){
			kattisInit(dataPath+"maffigttest.indata",null);
		}else{
			kattisInit();
		}
		readBiPartiteGraph();
		try {
			g = new MaxFlow().run(edgesA, edgesB, edgesCap, numVertices, src, sink, numEdges);
		} catch (Exception e) {
			e.printStackTrace();
		}
		getFlowSolutionPrintMatchSolution();
		io.flush();
		io.close();

	}
	private void readBiPartiteGraph() {
		int xNumber =  io.getInt();
		int yNumber = io.getInt();

		graphX = xNumber;
		graphY = yNumber;

		int numberOfEdgesBipGraph = io.getInt();
		int numberOfEdges = numberOfEdgesBipGraph+yNumber+xNumber;
		
		edgesA = new int[numberOfEdges];
		edgesB = new int[numberOfEdges];
		edgesCap = new int[numberOfEdges];

		numVertices = xNumber+yNumber+2;
		sink = numVertices;
		src = 1;
		numEdges = numberOfEdges;
		int edgesIndex = 0;
		for(int i=2;i<=(xNumber+1);i++,edgesIndex++){
			edgesA[edgesIndex] = 1;
			edgesB[edgesIndex] = i;
			edgesCap[edgesIndex] = 1;
		}
		//Edges mellan X o Y
		for(int i=0;i<numberOfEdgesBipGraph;i++,edgesIndex++){
			int from = io.getInt()+1;
			int to = io.getInt()+1;
			edgesA[edgesIndex] = from;
			edgesB[edgesIndex] = to;
			edgesCap[edgesIndex] = 1;
		}
		for(int i=xNumber+2;i<numVertices;i++,edgesIndex++){
			edgesA[edgesIndex] = i;
			edgesB[edgesIndex] = sink;
			edgesCap[edgesIndex] = 1;
		}
		io.flush();
		

	}
	private void getFlowSolutionPrintMatchSolution(){
		int maxFlow = 0;
		Edge srcEdges = g.getEdgesForth(src);
		while(srcEdges != null){
			Edge e = srcEdges;
			maxFlow += e.flow;
			srcEdges = srcEdges.next;
		}
		int[] posEdgesFrom = new int[numEdges+1];
		int[] posEdgesTo = new int[numEdges+1];
		int[] posEdgesFlow = new int[numEdges+1];
		Edge[] allVertices = g.getEdges();
		int posEdgeIndex = 0;
		for(int i=0;i<allVertices.length;i++){
			Edge vertexEdges = allVertices[i];
			while(vertexEdges != null){
				Edge e = vertexEdges;
				if(e.flow>0){
					posEdgesFrom[posEdgeIndex] = e.from;
					posEdgesTo[posEdgeIndex] = e.to;
					posEdgesFlow[posEdgeIndex] = e.flow;
					posEdgeIndex++;
				}
				vertexEdges = vertexEdges.next;
			}


		}
		printBipMatchSolution(posEdgesFrom,posEdgesTo,maxFlow);
	}
	void printBipMatchSolution(int[] eFrom, int[] eTo,int totFlow) {
		io.println(graphX + " " + graphY);
		io.println(totFlow);

		for (int i = 0; eFrom[i] != 0; ++i) {
			int a = eFrom[i];
			int b = eTo[i];
			if(a != src && b != sink){
				io.println((a-1)+" "+(b-1));
			}
		}
	}
	private void kattisInit(String fileInPath,String fileOutPath){
		InputStream i = null;
		OutputStream o = null;
		try {
			i = new FileInputStream(new File(fileInPath));
			if(fileOutPath != null){
				o = new FileOutputStream(new File(fileOutPath));
			}else{
				o = System.out;
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		io = new Kattio(i,o);
	}
	private void kattisInit(){
		io =new Kattio(System.in,System.out);
	}
}
