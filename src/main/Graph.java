package src.main;
import java.util.ArrayList;

public class Graph {
	private ArrayList<Node> nodes;
	
	// We consider that the first and the last nodes are always fixed
	private Node source;		// First nodes (labeled 1)
	private Node dest;			// Last nodes


	public Graph(){
		this.nodes = new ArrayList<Node>();
		this.source = null;
		this.dest = null;
	}
	
	public Graph(ArrayList<Node> nodes){
		this.nodes = nodes;
		this.finalizeGraph();
	}

	// No sanity check on actual first and last
	public Graph(ArrayList<Node> nodes, Node source, Node dest){
		this.nodes = nodes;
		this.source = source;
		this.dest = dest;
	}


	public ArrayList<Node> getNodes(){
		return this.nodes;
	}

	public Node getSource(){
		return this.source;
	}

	public Node getDestination(){
		return this.dest;
	}


	public void addNode(Node n){
		this.nodes.add(n);
	}

	// Finalize the graph (MANDATORY when created with empty constructor)
	// Sets the source and destination
	public void finalizeGraph(){
		this.source = getMinNode(this.nodes);
		this.dest = getMaxNode(this.nodes);
	}


	// Verify if the graph is a valid solution to the Rikudo problem
	public boolean verify(){
		int n = this.getNodes().size();


		// This loop checks if the path is correct (not considering diamonds, only labels incrementing)
		Node node = this.getSource();
		for(int i = 1; i < n; i++){
			// We check if the node is correctly labeled
			if(node.getLabel() != i) { return false;}

			// We retrieve the next node in the sequence (labeled higher, if it exists)
			Node nextNode = node.getNextNeighbor();
			if(nextNode == null) { return false;}

			node = nextNode;			
		}	
		// Check normally the last one (destination, should be fine)
		if(node.getLabel() != n) { return false;}


		// Checks if diamonds are respected
		ArrayList<Node[]> allDiams = this.getDiamonds();
		for(Node[] pairs : allDiams){
			// Not labeled 1 apart
			if(Math.abs(pairs[0].getLabel() - pairs[1].getLabel()) != 1) { return false;}
		}

		return true;
	}


	/**
	 * @implNote Will contain twice each diamonds (due to reverse)
	 * @return ArrayList containing pairs of nodes that are linked with a diamond
	 */
	public ArrayList<Node[]> getDiamonds(){
		ArrayList<Node> nodes = this.getNodes();

		ArrayList<Node[]> allDiams = new ArrayList<Node[]>();

		for(Node n : nodes){
			Node[] diams = n.getActualDiamondNodes();
			for(Node n2 : diams){
				allDiams.add(new Node[] {n, n2});
			}
		}

		return allDiams;
	}

	// *********************** PRIVATE ***********************

	// Computes the last node of the array (with max label)
	private static Node getMaxNode(ArrayList<Node> nodes){
		int maxLabel = -1;
		Node retNode = null;
		for(Node n : nodes){
			int l = n.getLabel();
			if(l > maxLabel){
				maxLabel = l;
				retNode = n;
			}
		}
		return retNode;
	}

	// Computes the first node of the array (with min label)
	private static Node getMinNode(ArrayList<Node> nodes){
		Node retNode = nodes.get(0);
		int minLabel = retNode.getLabel();
		for(int i = 1; i < nodes.size(); i++){
			Node n = nodes.get(i);
			int l = n.getLabel();
			if(l < minLabel && l > 0){
				minLabel = l;
				retNode = n;
			}
		}
		return retNode;
	}



	public static void pp(Graph g){
		ArrayList<Node> nodes = g.getNodes();
		for(int i=0;i<nodes.size();i++){
			Node.pp(nodes.get(i));
		}
	}
}