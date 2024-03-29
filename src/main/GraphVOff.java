package src.main;

import java.awt.Color;
import java.util.ArrayList;

public class GraphVOff extends GraphV {
	
	private NodeVOff source, bottom;
	
	/**
	 * Generates a graph used in the visualizer but only as a support for map creation, at first we use it as a grid
	 * then we can extract the toggled/existing/used nodes to create a graph.
	 * @param g the graph to build.
	 * @param all_toggled in the generation we can already toggle all nodes in the given graph.
	 */
	public GraphVOff(Graph g, boolean all_toggled) {
		super(g);
		this.nodes = new NodeVOff[g.getNodes().size()];
		
		for (Node node : graph.getNodes()) {
			nodes[node.id] = new NodeVOff(node); //false id true id
			if (all_toggled) ((NodeVOff) nodes[node.id]).toggleOn();
			if (g.getSource() == node) {
				setSource((NodeVOff) nodes[node.id]);
				((NodeVOff) nodes[node.id]).toggleOn();
			}
			else if (g.getDestination() == node) {
				setBottom((NodeVOff) nodes[node.id]);
				((NodeVOff) nodes[node.id]).toggleOn();
			}
		}
		
		//we consider the source of the graph to be at the center of the screen
		nodes[g.getSource().id].setPosition(0, 0);
		//we want to set the position to other cells relative to the source
		DFS(g.getSource().id);
	}
	
	public NodeVOff getSource() {
		return this.source;
	}
	
	public boolean hasSource() {
		return this.source != null;
	}
	
	public NodeVOff getBottom() {
		return this.bottom;
	}
	
	public boolean hasBottom() {
		return this.bottom != null;
	}
	
	public boolean isSource(NodeVOff node) {
		return this.source == node;
	}
	
	public boolean isBottom(NodeVOff node) {
		return this.bottom == node;
	}
	
	public void setSource(NodeVOff node) {
		System.out.println("Source set");
		if (node == null) {
			if (source != null) {
				source.getNode().setLabel(-1);
				source.getNode().setIsFixed(false);
				source.forceColor(null);
			}
			source = null;
			return;
		}
		node.getNode().setLabel(1);
		node.getNode().setIsFixed(true);
		node.forceColor(Color.RED);
		this.graph.setSource(node.getNode());
		this.source = node;
	}
	
	public void setBottom(NodeVOff node) {
		System.out.println("Bottom set");
		if (node == null) {
			if (bottom != null) {
				bottom.getNode().setLabel(-1);
				bottom.getNode().setIsFixed(false);
				bottom.forceColor(null);
			}
			bottom = null;
			return;
		}
		node.getNode().setLabel(NodeVOff.COUNTER);
		node.getNode().setIsFixed(true);
		node.forceColor(Color.GREEN);
		this.graph.setDestination(node.getNode());
		this.bottom = node;
	}
	
	/**
	 * Export the graph created using DFS to copy the graph containing existing nodes.
	 * The graph must be correct : connected and have source and destination points.
	 * @return existing graph
	 */
	public Graph export() {
		Node.COUNTER = 0;
		if (source == null || bottom == null) {
			System.err.println(Visualizer.prefix + "Error, cannot export an incomplete graph, insert source/destination.");
			return null;
		}
		Graph copy_graph = DFS_2(source.getNode().id);
		return copy_graph;
	}
	
	private static int i = 0;
	
	/*
	 * Recursive DFS used in DFS(int v) to create the exported graph.
	 */
    private void DFS_bis_2(int cur, boolean visited[], Node[] copied, Graph exported_graph)
    {
    	NodeV node = this.nodes[cur];
    	if (!((NodeVOff) node).exists()) return; //if the node isn't part of the exported graph
    	
    	//we check if we already copied the node or not
    	Node copy;
    	if (copied[node.getNode().id] == null) {
    		copy = new Node(node.getNode(), i);
    		//we save the copy into the nodes we visited with the id of the original node
    		//this will allow us to know which node has been worked on already or not
    		//while keeping track of the copies
    		copied[node.getNode().id] = copy;
    		i++; //id increment
    	} else copy = copied[node.getNode().id];
    	
    	visited[node.getNode().id] = true;
    	
    	for (int k = 0; k < 6; k++) { //we reset the neighbors of the copy which were neighbors of the original graph
    		Node neigh = node.getNode().getNeighbor(Node.getDirection(k));
    		if (neigh != null) {
	    		Node neigh_copy = copied[neigh.id];
	    		if (neigh_copy == null) {//if the neighbor has not yet been copied
	    			copied[neigh.id] = new Node(neigh, i);
	    			i++; //id increment
	    			neigh_copy = copied[neigh.id];
	    		}
	    		Node.linkNodes(copy, neigh_copy, Node.getDirection(k)); //we link the copied nodes
    		} else copy.setNeighbor(null, Node.getDirection(k));
    	}
    	
    	/*if (father != null) //the given father should be a node in the exported graph
    		Node.linkNodes(father, copy, dir);*/
    	
    	if (graph.getSource() == node.getNode()) {
    		exported_graph.setSource(copy);
    	}
    	else if (graph.getDestination() == node.getNode()) exported_graph.setDestination(copy);
    	
    	exported_graph.addNode(copy);

    	//for all neighbors of the original node node
        int counter = 0;
        
        while (counter < 6) {
        	Node neigh = node.getNode().getNeighbors()[counter];
        	if (neigh != null) {
	            int next = neigh.id;
	            if (((NodeVOff) nodes[next]).exists()) { //might save some calls
	            	if (!visited[next])
	            		DFS_bis_2(next, visited, copied, exported_graph);
	            }
        	}
            counter++; //next neighbor
        }
    }
 
    /**
     * Deep First Search
     * @param v
     */
    private Graph DFS_2(int v)
    {
        //by default all items are false
    	i=0;
        Node copied[] = new Node[graph.getNodes().size()];
        boolean visited[] = new boolean[graph.getNodes().size()];
        Graph exported_graph = new Graph();
        DFS_bis_2(v, visited, copied, exported_graph);
        if (exported_graph.getSource() == null || exported_graph.getDestination() == null) {
        	System.err.println(Visualizer.prefix + "Error, did not find source or destination in exported graph.\nThe graph may be not connected.");
        	return null;
        }
        return exported_graph;
    }

}
